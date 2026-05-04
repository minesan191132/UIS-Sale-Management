package org.example.features.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderRepository;
import org.example.features.payment.dto.MilestoneQrDTO;
import org.example.features.payment.dto.PaymentSummaryDTO;
import org.example.features.payment.dto.SepayWebhookDTO;
import org.example.features.payment.entity.Payment;
import org.example.features.payment.entity.PaymentMethod;
import org.example.features.payment.entity.PaymentMilestone;
import org.example.features.payment.entity.PaymentType;
import org.example.features.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Service for managing payments with SePay integration
 *
 * SePay Quick Link format:
 * https://qr.sepay.vn/img?acc={STK}&bank={BANK_CODE}&amount={AMOUNT}&des={CONTENT}&template=compact
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMilestoneService paymentMilestoneService;
    private final UserNotificationService userNotificationService;
    private final org.example.features.realtime.service.SseService sseService;

    @Value("${sepay.bank.account}")
    private String bankAccount;

    @Value("${sepay.bank.code}")
    private String bankCode;

    private static final String SEPAY_QR_BASE = "https://qr.sepay.vn/img";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Pattern để tìm mã thanh toán trong nội dung chuyển khoản
    // Khớp với: COC25A0305250083 (format mới, không dấu gạch ngang)
    // Hoặc COC-ORD-YYYYMMDD-XXX (format cũ, để tương thích)
    private static final Pattern ORDER_CODE_PATTERN = Pattern.compile(
            "(?i)COC[-\\s]?([A-Z0-9]{6,})", Pattern.CASE_INSENSITIVE);

    // Milestone transfer content format: MS{milestoneId}PAY{orderId}
    private static final Pattern MILESTONE_PATTERN = Pattern.compile(
            "(?i)MS\\s*(\\d+)\\s*PAY\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    // =====================================================
    // QR GENERATION
    // =====================================================

    /**
     * Tạo URL ảnh QR SePay Quick Link cho đơn hàng.
     * Không cần đăng ký SePay, dùng ngay.
     *
     * Format nội dung chuyển khoản: COC-{orderNumber}
     * Ví dụ: COC-ORD-20240115-001
     */
    public String generateSepayQrUrl(String orderNumber, BigDecimal depositAmount) {
        String description = generateTransferContent(orderNumber);

        String url = UriComponentsBuilder.fromHttpUrl(SEPAY_QR_BASE)
                .queryParam("acc", bankAccount)
                .queryParam("bank", bankCode)
                .queryParam("amount", depositAmount.longValue())
                .queryParam("des", description)
                .queryParam("template", "compact")
                .build()
                .toUriString();

        log.info("Generated SePay QR URL for order {}: amount={}", orderNumber, depositAmount);
        return url;
    }

    /**
     * Tạo nội dung chuyển khoản ngắn gọn, không dấu đặc biệt.
     * Ví dụ: ORD-25-A0305-25-0083 → COC25A0305250083
     */
    public String generateTransferContent(String orderNumber) {
        String cleaned = orderNumber.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        return "COC" + cleaned;
    }

    // =====================================================
    // WEBHOOK HANDLING
    // =====================================================

    /**
     * Xử lý webhook từ SePay khi có giao dịch ngân hàng.
     *
     * SePay gọi POST về endpoint này với thông tin giao dịch.
     * Tự động:
     * 1. Tìm đơn hàng theo nội dung chuyển khoản
     * 2. Tạo Payment record
     * 3. Cập nhật đơn hàng -> DEPOSITED
     */
    @Transactional
    public WebhookResult handleSepayWebhook(SepayWebhookDTO webhook) {
        log.info("Received SePay webhook: id={}, amount={}, content={}",
                webhook.getId(), webhook.getTransferAmount(), webhook.getContent());

        // 1. Chỉ xử lý giao dịch tiền vào
        if (!webhook.isMoneyIn()) {
            log.info("Ignoring outgoing transaction: {}", webhook.getId());
            return WebhookResult.ignored("Not an incoming transaction");
        }

        // 2. Kiểm tra số tài khoản
        if (!bankAccount.equals(webhook.getAccountNumber())) {
            log.warn("Account mismatch: expected={}, got={}", bankAccount, webhook.getAccountNumber());
            return WebhookResult.ignored("Account number mismatch");
        }

        // 3. Milestone flow for CUSTOM_MANUFACTURING: MS{milestoneId}PAY{orderId}
        MilestoneReference milestoneRef = extractMilestoneReference(webhook.getContent());
        if (milestoneRef == null) {
            milestoneRef = extractMilestoneReference(webhook.getCode());
        }

        if (milestoneRef != null) {
            PaymentMilestoneService.MilestoneWebhookResult milestoneResult = paymentMilestoneService.recordMilestoneWebhook(
                    webhook,
                    milestoneRef.milestoneId(),
                    milestoneRef.orderId());

            return WebhookResult.milestone(
                    milestoneResult.status(),
                    milestoneResult.message(),
                    milestoneResult.orderId(),
                    milestoneResult.milestoneId(),
                    milestoneResult.amount());
        }

        // 4. Legacy flow for READY_MADE and backward compatibility (COC...)
        String cleanedCode = extractOrderNumber(webhook.getContent());
        if (cleanedCode == null) {
            cleanedCode = extractOrderNumber(webhook.getCode());
        }

        if (cleanedCode == null) {
            log.warn("Cannot extract order number from webhook content: '{}'", webhook.getContent());
            return WebhookResult.failed("Cannot identify order from transfer content");
        }

        final String finalCleanedCode = cleanedCode;

        // 5. Tìm đơn hàng bằng cách match cleaned order number
        // So sánh phần sau COC với orderNumber đã xoá ký tự đặc biệt
        Optional<Order> orderOpt = orderRepository.findAll().stream()
                .filter(o -> o.getOrderNumber() != null &&
                        o.getOrderNumber().replaceAll("[^A-Za-z0-9]", "").equalsIgnoreCase(finalCleanedCode))
                .findFirst();
        if (orderOpt.isEmpty()) {
            // Thử tìm trực tiếp (format cũ)
            orderOpt = orderRepository.findByOrderNumber(finalCleanedCode);
        }
        if (orderOpt.isEmpty()) {
            log.warn("Order not found for cleaned code: {}", finalCleanedCode);
            return WebhookResult.failed("Order not found: " + finalCleanedCode);
        }

        Order order = orderOpt.get();

        if (!paymentMilestoneService.getMilestones(order.getId()).isEmpty()) {
            return WebhookResult.ignored("Order uses milestone payment flow. Please transfer with MS{milestoneId}PAY{orderId} content.");
        }

        // 6. Kiểm tra trạng thái đơn hàng và xác định loại thanh toán
        OrderStatus currentStatus = order.getStatus();
        PaymentType paymentType = PaymentType.DEPOSIT; // Mặc định
        boolean isSecondPayment = false;

        if (currentStatus == OrderStatus.AWAITING_PAYMENT) {
            paymentType = PaymentType.DEPOSIT;
        } else if (currentStatus == OrderStatus.PROCESSING || currentStatus == OrderStatus.AWAITING_REMAINING_PAYMENT) {
            // Thanh toán đợt 2 cho đơn gia công
            paymentType = PaymentType.FINAL;
            isSecondPayment = true;
        } else {
            log.warn("Order {} is not in AWAITING_PAYMENT/PROCESSING/AWAITING_REMAINING_PAYMENT status (current: {})",
                    order.getOrderNumber(), currentStatus);
            return WebhookResult.ignored("Order is not awaiting payment (status: " + currentStatus + ")");
        }

        // 7. Tính toán số tiền cần thiết dựa theo loại thanh toán
        BigDecimal amountRequired = BigDecimal.ZERO;
        if (!isSecondPayment) {
            // Thanh toán đợt 1: deposit
            amountRequired = order.getDepositAmount() != null ? order.getDepositAmount() : BigDecimal.ZERO;
        } else {
            // Thanh toán đợt 2: số tiền còn lại = total - deposit
            if (order.getTotalPrice() != null && order.getDepositAmount() != null) {
                amountRequired = order.getTotalPrice().subtract(order.getDepositAmount());
            }
        }

        // 8. Kiểm tra số tiền (cho phép ±1% sai số làm tròn)
        BigDecimal tolerance = amountRequired.multiply(BigDecimal.valueOf(0.01));
        BigDecimal diff = webhook.getTransferAmount().subtract(amountRequired).abs();
        if (diff.compareTo(tolerance) > 0 && webhook.getTransferAmount().compareTo(amountRequired) < 0) {
            log.warn("Insufficient payment for order {}: required={}, received={}",
                    order.getOrderNumber(), amountRequired, webhook.getTransferAmount());
            // Vẫn record payment nhưng không đổi trạng thái
            recordPaymentOnly(order, webhook, PaymentType.PARTIAL);
            return WebhookResult.partialPayment("Payment recorded but insufficient for confirmation");
        }

        // 9. Tạo Payment record
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(webhook.getTransferAmount());
        payment.setPaymentType(paymentType);
        payment.setPaymentMethod(PaymentMethod.SEPAY);
        payment.setTransactionRef(webhook.getReferenceCode());
        payment.setVerifiedAt(LocalDateTime.now());
        payment.setNotes("SePay webhook - " + webhook.getTransactionDate() + " - " + webhook.getContent());
        paymentRepository.save(payment);

        // 10. Cập nhật đơn hàng
        if (!isSecondPayment) {
            // Thanh toán đợt 1: AWAITING_PAYMENT -> DEPOSITED
            order.setStatus(OrderStatus.DEPOSITED);
            order.setPaidAt(LocalDateTime.now());

            userNotificationService.pushOrderNotification(
                    order,
                    NotificationType.ORDER,
                    "Đã nhận tiền cọc",
                    "Cảm ơn bạn đã cọc trước cho đơn " + order.getOrderNumber() + ". Đơn hàng sẽ sớm được xử lý.",
                    "order-status-" + order.getId() + "-DEPOSITED");

            log.info("✅ Deposit confirmed for order {}: amount={}", order.getOrderNumber(), webhook.getTransferAmount());
        } else {
            // Thanh toán đợt 2: Cộng dồn vào deposit_amount
            if (order.getDepositAmount() == null) {
                order.setDepositAmount(webhook.getTransferAmount());
            } else {
                order.setDepositAmount(order.getDepositAmount().add(webhook.getTransferAmount()));
            }

            // Nếu đã thanh toán đủ 100%, tự động chuyển sang Chờ giao hàng
            if (order.getTotalPrice() != null &&
                order.getDepositAmount().compareTo(order.getTotalPrice()) >= 0) {
                order.setStatus(OrderStatus.AWAITING_DELIVERY);
                order.setPaidAt(LocalDateTime.now());

                userNotificationService.pushOrderNotification(
                        order,
                        NotificationType.ORDER,
                        "Đơn hàng chờ giao",
                        "Đơn " + order.getOrderNumber() + " đã thanh toán đầy đủ và đang chờ giao hàng.",
                        "order-status-" + order.getId() + "-AWAITING_DELIVERY");

                log.info("✅ Full 2nd payment confirmed → AWAITING_DELIVERY: order={}, total={}/{}",
                    order.getOrderNumber(), order.getDepositAmount(), order.getTotalPrice());
            } else {
                log.info("✅ Partial 2nd payment recorded for order {}: amount={}",
                    order.getOrderNumber(), webhook.getTransferAmount());
            }
        }

        orderRepository.save(order);
        sseService.sendEvent("ORDER_UPDATED", order.getId());
        return WebhookResult.success(order.getOrderNumber(), order.getId(), webhook.getTransferAmount());
    }

    /**
     * Trích xuất phần mã sau tiền tố COC từ nội dung chuyển khoản.
     * Hỗ trợ:
     * - Format mới: "COC25A0305250083" → "25A0305250083"
     * - Format cũ:  "COC-ORD-20240115-001" → "ORD20240115001" (đã clean)
     */
    private String extractOrderNumber(String content) {
        if (content == null || content.isBlank())
            return null;

        Matcher matcher = ORDER_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            // Trả về phần alphanumeric sau COC (đã xoá ký tự đặc biệt)
            return matcher.group(1).replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        }
        return null;
    }

    private MilestoneReference extractMilestoneReference(String content) {
        if (content == null || content.isBlank()) {
            return null;
        }

        Matcher matcher = MILESTONE_PATTERN.matcher(content);
        if (!matcher.find()) {
            return null;
        }

        try {
            Long milestoneId = Long.parseLong(matcher.group(1));
            Long orderId = Long.parseLong(matcher.group(2));
            return new MilestoneReference(milestoneId, orderId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Chỉ ghi nhận payment (không đổi trạng thái đơn hàng)
     */
    private void recordPaymentOnly(Order order, SepayWebhookDTO webhook, PaymentType type) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(webhook.getTransferAmount());
        payment.setPaymentType(type);
        payment.setPaymentMethod(PaymentMethod.SEPAY);
        payment.setTransactionRef(webhook.getReferenceCode());
        payment.setNotes("Partial - SePay webhook - " + webhook.getContent());
        paymentRepository.save(payment);
    }

    // =====================================================
    // PAYMENT SUMMARY
    // =====================================================

    /**
     * Lấy thông tin QR và thanh toán cho đơn hàng
     */
    @Transactional
    public PaymentInfoDTO getPaymentInfo(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        BigDecimal totalPaid = paymentRepository.getTotalVerifiedPaymentsByOrder(orderId);

        String qrUrl = order.getPaymentQrUrl();
        String transferContent = generateTransferContent(order.getOrderNumber());
        BigDecimal payableAmount = order.getDepositAmount();

        Optional<PaymentMilestone> activeMilestone = paymentMilestoneService.findCurrentActiveMilestone(orderId);
        if (activeMilestone.isPresent()) {
            PaymentMilestone milestone = activeMilestone.get();
            MilestoneQrDTO milestoneQr = paymentMilestoneService.getMilestoneQr(milestone.getId());
            qrUrl = milestoneQr.getQrUrl();
            transferContent = milestoneQr.getTransferContent();
            payableAmount = milestone.getAmount();
        } else {
            // Luôn tái tạo QR URL từ config hiện tại (không dùng URL cũ trong DB)
            // -> Đổi bank code trong application.properties là đủ, không cần báo giá lại
            String freshQrUrl = null;
            if (order.getDepositAmount() != null) {
                freshQrUrl = generateSepayQrUrl(order.getOrderNumber(), order.getDepositAmount());
                // Cập nhật vào DB nếu URL thay đổi
                if (!freshQrUrl.equals(order.getPaymentQrUrl())) {
                    order.setPaymentQrUrl(freshQrUrl);
                    orderRepository.save(order);
                    log.info("QR URL refreshed for order {}", order.getOrderNumber());
                }
            }

            qrUrl = freshQrUrl != null ? freshQrUrl : order.getPaymentQrUrl();
        }

        List<PaymentItemDTO> paymentItems = payments.stream()
                .map(p -> new PaymentItemDTO(
                        p.getId(),
                        p.getAmount(),
                        p.getPaymentType() != null ? p.getPaymentType().name() : null,
                        p.getPaymentMethod() != null ? p.getPaymentMethod().name() : null,
                        p.getTransactionRef(),
                        p.getCreatedAt() != null ? p.getCreatedAt().format(FORMATTER) : null,
                        p.getVerifiedAt() != null ? p.getVerifiedAt().format(FORMATTER) : null,
                        p.isVerified()))
                .collect(Collectors.toList());

        return new PaymentInfoDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                payableAmount,
                totalPaid,
                order.getStatus().name(),
                qrUrl,
                transferContent,
                bankAccount,
                "DANG TRAN HOANG ANH",
                bankCode,
                paymentItems);
    }

    /**
     * Lấy payment summary (giữ tương thích với code cũ)
     */
    public PaymentSummaryDTO getPaymentSummary(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        BigDecimal totalPaid = paymentRepository.getTotalVerifiedPaymentsByOrder(orderId);
        BigDecimal totalPrice = order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO;
        BigDecimal depositRequired = order.getDepositAmount() != null ? order.getDepositAmount() : BigDecimal.ZERO;

        List<PaymentSummaryDTO.PaymentHistoryDTO> history = payments.stream()
                .map(p -> new PaymentSummaryDTO.PaymentHistoryDTO(
                        p.getId(),
                        p.getAmount(),
                        p.getPaymentType().toString(),
                        p.getPaymentMethod().toString(),
                        p.getTransactionRef(),
                        p.getCreatedAt().format(FORMATTER),
                        p.getVerifiedAt() != null ? p.getVerifiedAt().format(FORMATTER) : null,
                        p.isVerified()))
                .collect(Collectors.toList());

        return new PaymentSummaryDTO(
                orderId,
                order.getOrderNumber(),
                totalPrice,
                depositRequired,
                totalPaid,
                totalPrice.subtract(totalPaid),
                order.getStatus().name(),
                history);
    }

    public List<Payment> getPaymentHistory(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getUnverifiedPayments() {
        return paymentRepository.findUnverifiedPayments();
    }

    // =====================================================
    // RESULT DTOs
    // =====================================================

    /**
     * Kết quả xử lý webhook
     */
    public record WebhookResult(
            String status,
            String message,
            String orderNumber,
            Long orderId,
            Long milestoneId,
            BigDecimal amount) {
        public static WebhookResult success(String orderNumber, Long orderId, BigDecimal amount) {
            return new WebhookResult("SUCCESS", "Deposit confirmed", orderNumber, orderId, null, amount);
        }

        public static WebhookResult ignored(String reason) {
            return new WebhookResult("IGNORED", reason, null, null, null, null);
        }

        public static WebhookResult failed(String reason) {
            return new WebhookResult("FAILED", reason, null, null, null, null);
        }

        public static WebhookResult partialPayment(String msg) {
            return new WebhookResult("PARTIAL", msg, null, null, null, null);
        }

        public static WebhookResult milestone(String status,
                                              String message,
                                              Long orderId,
                                              Long milestoneId,
                                              BigDecimal amount) {
            return new WebhookResult(status, message, null, orderId, milestoneId, amount);
        }
    }

    private record MilestoneReference(Long milestoneId, Long orderId) {
    }

    /**
     * Thông tin thanh toán đầy đủ cho FE hiển thị QR
     */
    public record PaymentInfoDTO(
            Long orderId,
            String orderNumber,
            BigDecimal totalPrice,
            BigDecimal depositAmount,
            BigDecimal totalPaid,
            String orderStatus,
            String qrUrl,
            String transferContent,
            String bankAccount,
            String accountName,
            String bankName,
            List<PaymentItemDTO> paymentHistory) {
    }

    /**
     * Simple payment item DTO - không chứa JPA entity để tránh circular reference
     */
    public record PaymentItemDTO(
            Long id,
            BigDecimal amount,
            String paymentType,
            String paymentMethod,
            String transactionRef,
            String createdAt,
            String verifiedAt,
            boolean verified) {
    }
}
