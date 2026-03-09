package org.example.features.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderRepository;
import org.example.features.payment.dto.PaymentSummaryDTO;
import org.example.features.payment.dto.SepayWebhookDTO;
import org.example.features.payment.entity.Payment;
import org.example.features.payment.entity.PaymentMethod;
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

    @Value("${sepay.bank.account}")
    private String bankAccount;

    @Value("${sepay.bank.code}")
    private String bankCode;

    @Value("${sepay.webhook.token:}")
    private String webhookToken;

    private static final String SEPAY_QR_BASE = "https://qr.sepay.vn/img";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Pattern để tìm mã đơn trong nội dung chuyển khoản
    // Khớp với: COC-ORD-YYYYMMDD-XXX hoặc COC ORD-YYYYMMDD-XXX
    private static final Pattern ORDER_CODE_PATTERN = Pattern.compile("(?i)COC[-\\s]?(ORD[-\\w]+)",
            Pattern.CASE_INSENSITIVE);

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
        String description = "COC-" + orderNumber;

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
     * Tạo nội dung chuyển khoản chuẩn cho đơn hàng
     */
    public String generateTransferContent(String orderNumber) {
        return "COC-" + orderNumber;
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

        // 3. Tìm mã đơn hàng trong nội dung
        String orderNumber = extractOrderNumber(webhook.getContent());
        if (orderNumber == null) {
            orderNumber = extractOrderNumber(webhook.getCode());
        }

        if (orderNumber == null) {
            log.warn("Cannot extract order number from webhook content: '{}'", webhook.getContent());
            return WebhookResult.failed("Cannot identify order from transfer content");
        }

        final String finalOrderNumber = orderNumber;

        // 4. Tìm đơn hàng
        Optional<Order> orderOpt = orderRepository.findByOrderNumber(finalOrderNumber);
        if (orderOpt.isEmpty()) {
            log.warn("Order not found for number: {}", finalOrderNumber);
            return WebhookResult.failed("Order not found: " + finalOrderNumber);
        }

        Order order = orderOpt.get();

        // 5. Kiểm tra trạng thái đơn hàng
        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            log.warn("Order {} is not in AWAITING_PAYMENT status (current: {})",
                    finalOrderNumber, order.getStatus());
            return WebhookResult.ignored("Order is not awaiting payment (status: " + order.getStatus() + ")");
        }

        // 6. Kiểm tra số tiền (cho phép ±1% sai số làm tròn)
        BigDecimal depositRequired = order.getDepositAmount();
        if (depositRequired != null) {
            BigDecimal tolerance = depositRequired.multiply(BigDecimal.valueOf(0.01));
            BigDecimal diff = webhook.getTransferAmount().subtract(depositRequired).abs();
            if (diff.compareTo(tolerance) > 0 && webhook.getTransferAmount().compareTo(depositRequired) < 0) {
                log.warn("Insufficient deposit for order {}: required={}, received={}",
                        finalOrderNumber, depositRequired, webhook.getTransferAmount());
                // Vẫn record payment nhưng không đổi trạng thái
                recordPaymentOnly(order, webhook, PaymentType.PARTIAL);
                return WebhookResult.partialPayment("Payment recorded but insufficient for deposit confirmation");
            }
        }

        // 7. Tạo Payment record đầy đủ
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(webhook.getTransferAmount());
        payment.setPaymentType(PaymentType.DEPOSIT);
        payment.setPaymentMethod(PaymentMethod.SEPAY);
        payment.setTransactionRef(webhook.getReferenceCode());
        payment.setVerifiedAt(LocalDateTime.now()); // Tự động xác minh qua webhook
        payment.setNotes("SePay webhook - " + webhook.getTransactionDate()
                + " - content: " + webhook.getContent());
        paymentRepository.save(payment);

        // 8. Cập nhật đơn hàng sang DEPOSITED
        order.setStatus(OrderStatus.DEPOSITED);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);

        log.info("✅ Deposit confirmed for order {}: amount={}", finalOrderNumber, webhook.getTransferAmount());
        return WebhookResult.success(finalOrderNumber, webhook.getTransferAmount());
    }

    /**
     * Trích xuất mã đơn hàng từ nội dung chuyển khoản.
     * Hỗ trợ các format:
     * - "COC-ORD-20240115-001"
     * - "COC ORD-20240115-001"
     * - "DANG TRAN HOANG ANH chuyen COC-ORD-20240115-001"
     */
    private String extractOrderNumber(String content) {
        if (content == null || content.isBlank())
            return null;

        Matcher matcher = ORDER_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim().toUpperCase();
        }
        return null;
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

        // Luôn tái tạo QR URL từ config hiện tại (không dùng URL cũ trong DB)
        // → Đổi bank code trong application.properties là đủ, không cần báo giá lại
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

        String qrUrl = freshQrUrl != null ? freshQrUrl : order.getPaymentQrUrl();

        return new PaymentInfoDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getTotalPrice(),
                order.getDepositAmount(),
                totalPaid,
                order.getStatus().name(),
                qrUrl,
                generateTransferContent(order.getOrderNumber()),
                bankAccount,
                "DANG TRAN HOANG ANH",
                bankCode,
                payments);
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
            BigDecimal amount) {
        public static WebhookResult success(String orderNumber, BigDecimal amount) {
            return new WebhookResult("SUCCESS", "Deposit confirmed", orderNumber, amount);
        }

        public static WebhookResult ignored(String reason) {
            return new WebhookResult("IGNORED", reason, null, null);
        }

        public static WebhookResult failed(String reason) {
            return new WebhookResult("FAILED", reason, null, null);
        }

        public static WebhookResult partialPayment(String msg) {
            return new WebhookResult("PARTIAL", msg, null, null);
        }
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
            List<Payment> paymentHistory) {
    }
}
