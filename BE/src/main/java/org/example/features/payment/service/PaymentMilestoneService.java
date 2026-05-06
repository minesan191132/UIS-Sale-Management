package org.example.features.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.UserRepository;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderRepository;
import org.example.features.order.service.OrderAuditService;
import org.example.features.payment.dto.MilestoneListResponseDTO;
import org.example.features.payment.dto.MilestoneQrDTO;
import org.example.features.payment.dto.MilestoneVerifyResponseDTO;
import org.example.features.payment.dto.SepayWebhookDTO;
import org.example.features.payment.entity.MilestoneStatus;
import org.example.features.payment.entity.Payment;
import org.example.features.payment.entity.PaymentMethod;
import org.example.features.payment.entity.PaymentMilestone;
import org.example.features.payment.entity.PaymentType;
import org.example.features.payment.repository.PaymentMilestoneRepository;
import org.example.features.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.example.features.realtime.service.SseService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMilestoneService {

    private static final int DEFAULT_DUE_DAYS = 7;
    private static final String SEPAY_QR_BASE = "https://qr.sepay.vn/img";
    private static final String ACCOUNT_NAME = "DANG TRAN HOANG ANH";

    private final PaymentMilestoneRepository paymentMilestoneRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final UserNotificationService userNotificationService;
    private final OrderAuditService orderAuditService;
    private final SseService sseService;

    @Value("${sepay.bank.account}")
    private String bankAccount;

    @Value("${sepay.bank.code}")
    private String bankCode;

    @Transactional
    public List<PaymentMilestone> initializeMilestones(Order order) {
        if (order == null || order.getId() == null) {
            throw new IllegalArgumentException("Order is required");
        }

        List<PaymentMilestone> existingMilestones = paymentMilestoneRepository.findByOrderIdOrderByMilestoneOrderAsc(order.getId());
        
        if (!existingMilestones.isEmpty()) {
            // Nếu có milestone nhưng đã bị hủy hoặc chưa kích hoạt, khôi phục lại để có thể sử dụng
            boolean allPaid = existingMilestones.stream()
                    .allMatch(m -> m.getStatus() == MilestoneStatus.PAID);
            
            if (!allPaid) {
                // Cập nhật số tiền milestone dựa trên total price mới
                BigDecimal total = safe(order.getTotalPrice());
                BigDecimal firstAmount = order.getDepositAmount() != null
                        ? order.getDepositAmount()
                        : total.multiply(BigDecimal.valueOf(0.6)).setScale(0, RoundingMode.HALF_UP);
                BigDecimal secondAmount = total.subtract(firstAmount);
                if (secondAmount.compareTo(BigDecimal.ZERO) < 0) {
                    secondAmount = BigDecimal.ZERO;
                }
                
                // Khôi phục các milestone không phải PAID về PENDING
                // Để có thể sử dụng lại khi gửi báo giá mới sau khi từ chối hợp đồng
                boolean changed = false;
                for (int i = 0; i < existingMilestones.size(); i++) {
                    PaymentMilestone milestone = existingMilestones.get(i);
                    if (milestone.getStatus() != MilestoneStatus.PAID) {
                        milestone.setStatus(MilestoneStatus.PENDING);
                        milestone.setDueDate(null);
                        milestone.setPaymentQrUrl(null);
                        milestone.setPaidAt(null);
                        milestone.setTransactionRef(null);
                        milestone.setPaidAmount(null);
                        milestone.setVerifiedAt(null);
                        milestone.setVerifiedBy(null);
                        
                        // Cập nhật số tiền
                        if (milestone.getMilestoneOrder() == 1) {
                            milestone.setAmount(firstAmount);
                            milestone.setPercentage(BigDecimal.valueOf(60).setScale(2, RoundingMode.UNNECESSARY));
                        } else if (milestone.getMilestoneOrder() == 2) {
                            milestone.setAmount(secondAmount);
                            milestone.setPercentage(BigDecimal.valueOf(40).setScale(2, RoundingMode.UNNECESSARY));
                        }
                        changed = true;
                    }
                }
                if (changed) {
                    paymentMilestoneRepository.saveAll(existingMilestones);
                }
            }
            return existingMilestones;
        }

        BigDecimal total = safe(order.getTotalPrice());
        BigDecimal firstAmount = order.getDepositAmount() != null
                ? order.getDepositAmount()
                : total.multiply(BigDecimal.valueOf(0.6)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal secondAmount = total.subtract(firstAmount);
        if (secondAmount.compareTo(BigDecimal.ZERO) < 0) {
            secondAmount = BigDecimal.ZERO;
        }

        PaymentMilestone first = new PaymentMilestone();
        first.setOrder(order);
        first.setMilestoneOrder(1);
        first.setMilestoneName("Đặt cọc");
        first.setPercentage(BigDecimal.valueOf(60).setScale(2, RoundingMode.UNNECESSARY));
        first.setAmount(firstAmount);
        first.setStatus(MilestoneStatus.PENDING);

        PaymentMilestone second = new PaymentMilestone();
        second.setOrder(order);
        second.setMilestoneOrder(2);
        second.setMilestoneName("Thanh toán cuối");
        second.setPercentage(BigDecimal.valueOf(40).setScale(2, RoundingMode.UNNECESSARY));
        second.setAmount(secondAmount);
        second.setStatus(MilestoneStatus.PENDING);

        List<PaymentMilestone> milestones = new ArrayList<>();
        milestones.add(first);
        milestones.add(second);
        return paymentMilestoneRepository.saveAll(milestones);
    }

    @Transactional(readOnly = true)
    public List<PaymentMilestone> getMilestones(Long orderId) {
        return paymentMilestoneRepository.findByOrderIdOrderByMilestoneOrderAsc(orderId);
    }

    @Transactional
    public PaymentMilestone activateFirstMilestone(Long orderId) {
        return activateMilestone(orderId, 1);
    }

    @Transactional
    public PaymentMilestone activateSecondMilestone(Long orderId) {
        return activateMilestone(orderId, 2);
    }

    @Transactional
    public void cancelAllMilestones(Order order) {
        if (order == null || order.getId() == null) {
            return;
        }

        List<PaymentMilestone> milestones = paymentMilestoneRepository.findByOrderIdOrderByMilestoneOrderAsc(order.getId());
        boolean changed = false;
        for (PaymentMilestone milestone : milestones) {
            if (milestone.getStatus() != MilestoneStatus.PAID) {
                milestone.setStatus(MilestoneStatus.CANCELLED);
                milestone.setPaymentQrUrl(null);
                changed = true;
            }
        }

        if (changed) {
            paymentMilestoneRepository.saveAll(milestones);
        }
    }

    @Transactional
    public MilestoneWebhookResult recordMilestoneWebhook(SepayWebhookDTO webhook, Long milestoneId, Long orderId) {
        if (webhook.getReferenceCode() != null && !webhook.getReferenceCode().isBlank()) {
            Optional<PaymentMilestone> existingTx = paymentMilestoneRepository.findByTransactionRef(webhook.getReferenceCode());
            if (existingTx.isPresent()) {
                PaymentMilestone matched = existingTx.get();
                if (matched.getId().equals(milestoneId)) {
                    return new MilestoneWebhookResult(
                            "PENDING_VERIFICATION",
                            "Payment already recorded, waiting for admin verification",
                            matched.getId(),
                            matched.getOrder().getId(),
                            safe(matched.getPaidAmount()));
                }
                return new MilestoneWebhookResult("FAILED", "Transaction reference already used", null, null, null);
            }
        }

        PaymentMilestone milestone = paymentMilestoneRepository.findByIdAndOrderId(milestoneId, orderId)
                .orElse(null);
        if (milestone == null) {
            return new MilestoneWebhookResult("FAILED", "Milestone not found", null, null, null);
        }

        if (milestone.getStatus() == MilestoneStatus.PAID || milestone.getStatus() == MilestoneStatus.CANCELLED) {
            return new MilestoneWebhookResult("IGNORED", "Milestone already settled", milestone.getId(), orderId, null);
        }

        if (milestone.getStatus() != MilestoneStatus.ACTIVE
                && milestone.getStatus() != MilestoneStatus.OVERDUE
                && milestone.getStatus() != MilestoneStatus.PAID_UNVERIFIED) {
            return new MilestoneWebhookResult(
                    "IGNORED",
                    "Milestone is not accepting payments in current status",
                    milestone.getId(),
                    orderId,
                    null);
        }

        milestone.setStatus(MilestoneStatus.PAID_UNVERIFIED);
        milestone.setPaidAt(LocalDateTime.now());
        milestone.setTransactionRef(webhook.getReferenceCode());
        milestone.setPaidAmount(webhook.getTransferAmount());
        paymentMilestoneRepository.save(milestone);

        try {
            // Auto-verify instead of waiting for admin
            verifyMilestone(milestone.getId(), null);
        } catch (Exception e) {
            log.error("Failed to auto-verify milestone {}", milestone.getId(), e);
            notifyAdminsForVerification(milestone);
        }

        // Send realtime event so frontend can refresh
        sseService.sendEvent("ORDER_UPDATED", orderId);

        return new MilestoneWebhookResult(
                "SUCCESS",
                "Payment recorded and auto-verified",
                milestone.getId(),
                orderId,
                webhook.getTransferAmount());
    }

    @Transactional
    public MilestoneVerifyResponseDTO verifyMilestone(Long milestoneId, Long adminUserId) {
        PaymentMilestone milestone = paymentMilestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        if (milestone.getStatus() != MilestoneStatus.PAID_UNVERIFIED) {
            throw new IllegalStateException("Chỉ có thể xác nhận milestone ở trạng thái PAID_UNVERIFIED");
        }

        Order order = milestone.getOrder();
        OrderStatus previousOrderStatus = order.getStatus();
        boolean orderStatusChanged = false;

        LocalDateTime now = LocalDateTime.now();
        User adminUser = adminUserId == null ? null : userRepository.findById(adminUserId).orElse(null);

        milestone.setStatus(MilestoneStatus.PAID);
        milestone.setVerifiedAt(now);
        milestone.setVerifiedBy(adminUser);
        if (milestone.getPaidAmount() == null) {
            milestone.setPaidAmount(milestone.getAmount());
        }
        paymentMilestoneRepository.save(milestone);

        if (milestone.getMilestoneOrder() == 1) {
            if (order.getStatus() == OrderStatus.AWAITING_PAYMENT || order.getStatus() == OrderStatus.AWAITING_CONTRACT) {
                order.setStatus(OrderStatus.DEPOSITED);
                orderStatusChanged = true;
            }
            order.setPaymentDeadline(null);
            order.setPaymentQrUrl(null);
        } else if (milestone.getMilestoneOrder() == 2) {
            if (order.getStatus() == OrderStatus.AWAITING_REMAINING_PAYMENT) {
                order.setStatus(OrderStatus.AWAITING_DELIVERY);
                orderStatusChanged = true;
            }
            order.setPaymentDeadline(null);
            order.setPaymentQrUrl(null);
            order.setPaidAt(now);
        }

        orderRepository.save(order);
        saveVerifiedPayment(order, milestone, adminUser, now);

        if (orderStatusChanged) {
            orderAuditService.recordStatusEvent(
                    order,
                    OrderEventType.STATUS_CHANGED,
                    previousOrderStatus,
                    order.getStatus(),
                    adminUserId,
                    "ADMIN",
                    "Xác nhận thanh toán milestone " + milestone.getMilestoneOrder());
        }

        userNotificationService.pushOrderNotification(
                order,
                NotificationType.PAYMENT,
                "Đã xác nhận thanh toán",
                "Đã xác nhận thanh toán mốc '" + milestone.getMilestoneName() + "' cho đơn " + order.getOrderNumber() + ".",
                "milestone-verified-" + milestone.getId());

        MilestoneVerifyResponseDTO response = new MilestoneVerifyResponseDTO();
        response.setMilestoneId(milestone.getId());
        response.setMilestoneName(milestone.getMilestoneName());
        response.setStatus(milestone.getStatus().name());
        response.setPaidAmount(milestone.getPaidAmount());
        response.setTransactionRef(milestone.getTransactionRef());
        response.setVerifiedAt(milestone.getVerifiedAt());
        response.setVerifiedBy(adminUser != null ? adminUser.getFullName() : "Admin");
        response.setOrderStatusChanged(orderStatusChanged);
        response.setPreviousOrderStatus(previousOrderStatus.name());
        response.setNewOrderStatus(order.getStatus().name());
        response.setMessage("Đã xác nhận thanh toán mốc '" + milestone.getMilestoneName() + "'.");
        return response;
    }

    @Transactional(readOnly = true)
    public MilestoneListResponseDTO getMilestoneList(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<PaymentMilestone> milestones = paymentMilestoneRepository.findByOrderIdOrderByMilestoneOrderAsc(orderId);
        if (milestones.isEmpty()) {
            throw new IllegalArgumentException("No milestones found for this order");
        }

        BigDecimal totalPaid = milestones.stream()
                .filter(m -> m.getStatus() == MilestoneStatus.PAID)
                .map(m -> m.getPaidAmount() != null ? m.getPaidAmount() : m.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPrice = safe(order.getTotalPrice());
        BigDecimal remaining = totalPrice.subtract(totalPaid);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        int progress = 0;
        if (totalPrice.compareTo(BigDecimal.ZERO) > 0) {
            progress = totalPaid
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalPrice, 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        MilestoneListResponseDTO dto = new MilestoneListResponseDTO();
        dto.setOrderId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalPrice(totalPrice);
        dto.setTotalPaid(totalPaid);
        dto.setTotalRemaining(remaining);
        dto.setProgressPercent(progress);

        List<MilestoneListResponseDTO.MilestoneItemDTO> items = milestones.stream()
                .sorted(Comparator.comparing(PaymentMilestone::getMilestoneOrder))
                .map(this::toMilestoneItemDTO)
                .toList();
        dto.setMilestones(items);

        return dto;
    }

    @Transactional
    public MilestoneQrDTO getMilestoneQr(Long milestoneId) {
        PaymentMilestone milestone = paymentMilestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        if ((milestone.getStatus() == MilestoneStatus.ACTIVE || milestone.getStatus() == MilestoneStatus.OVERDUE)
                && (milestone.getPaymentQrUrl() == null || milestone.getPaymentQrUrl().isBlank())) {
            String qrUrl = generateMilestoneQrUrl(milestone.getId(), milestone.getOrder().getId(), milestone.getAmount());
            milestone.setPaymentQrUrl(qrUrl);
            paymentMilestoneRepository.save(milestone);
        }

        LocalDate dueDate = milestone.getDueDate();
        Long daysRemaining = null;
        boolean overdue = false;
        if (dueDate != null) {
            daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            overdue = dueDate.isBefore(LocalDate.now());
        }

        MilestoneQrDTO dto = new MilestoneQrDTO();
        dto.setMilestoneId(milestone.getId());
        dto.setOrderId(milestone.getOrder().getId());
        dto.setOrderNumber(milestone.getOrder().getOrderNumber());
        dto.setMilestoneName(milestone.getMilestoneName());
        dto.setAmount(milestone.getAmount());
        dto.setDueDate(dueDate);
        dto.setDaysRemaining(daysRemaining);
        dto.setOverdue(overdue);
        dto.setQrUrl(milestone.getPaymentQrUrl());
        dto.setTransferContent(buildTransferContent(milestone.getId(), milestone.getOrder().getId()));
        dto.setBankAccount(bankAccount);
        dto.setAccountName(ACCOUNT_NAME);
        dto.setBankName(bankCode);
        return dto;
    }

    @Transactional(readOnly = true)
    public Optional<PaymentMilestone> findCurrentActiveMilestone(Long orderId) {
        return paymentMilestoneRepository.findByOrderIdOrderByMilestoneOrderAsc(orderId)
                .stream()
                .filter(m -> m.getStatus() == MilestoneStatus.ACTIVE
                        || m.getStatus() == MilestoneStatus.OVERDUE
                        || m.getStatus() == MilestoneStatus.PAID_UNVERIFIED)
                .findFirst();
    }

    private PaymentMilestone activateMilestone(Long orderId, int milestoneOrder) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        PaymentMilestone milestone = paymentMilestoneRepository.findByOrderIdAndMilestoneOrder(orderId, milestoneOrder)
                .orElseThrow(() -> new IllegalArgumentException("Milestone not found"));

        if (milestone.getStatus() == MilestoneStatus.PAID) {
            return milestone;
        }
        if (milestone.getStatus() == MilestoneStatus.CANCELLED) {
            throw new IllegalStateException("Milestone has been cancelled");
        }

        LocalDate dueDate = LocalDate.now().plusDays(DEFAULT_DUE_DAYS);
        String qrUrl = generateMilestoneQrUrl(milestone.getId(), orderId, milestone.getAmount());

        milestone.setStatus(MilestoneStatus.ACTIVE);
        milestone.setDueDate(dueDate);
        milestone.setPaymentQrUrl(qrUrl);
        paymentMilestoneRepository.save(milestone);

        order.setPaymentDeadline(dueDate);
        order.setPaymentQrUrl(qrUrl);
        orderRepository.save(order);

        return milestone;
    }

    private MilestoneListResponseDTO.MilestoneItemDTO toMilestoneItemDTO(PaymentMilestone milestone) {
        MilestoneListResponseDTO.MilestoneItemDTO item = new MilestoneListResponseDTO.MilestoneItemDTO();
        item.setId(milestone.getId());
        item.setMilestoneOrder(milestone.getMilestoneOrder());
        item.setMilestoneName(milestone.getMilestoneName());
        item.setPercentage(milestone.getPercentage());
        item.setAmount(milestone.getAmount());
        item.setStatus(milestone.getStatus().name());
        item.setDueDate(milestone.getDueDate());
        item.setPaidAt(milestone.getPaidAt());
        item.setPaidAmount(milestone.getPaidAmount());
        item.setTransactionRef(milestone.getTransactionRef());
        item.setVerifiedAt(milestone.getVerifiedAt());
        item.setVerifiedBy(milestone.getVerifiedBy() != null ? milestone.getVerifiedBy().getFullName() : null);
        item.setPaymentQrUrl(milestone.getPaymentQrUrl());
        return item;
    }

    private void saveVerifiedPayment(Order order, PaymentMilestone milestone, User adminUser, LocalDateTime verifiedAt) {
        String transactionRef = milestone.getTransactionRef();
        if (transactionRef != null && !transactionRef.isBlank() && paymentRepository.existsByTransactionRef(transactionRef)) {
            return;
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(milestone.getPaidAmount() != null ? milestone.getPaidAmount() : milestone.getAmount());
        payment.setPaymentType(milestone.getMilestoneOrder() == 1 ? PaymentType.DEPOSIT : PaymentType.FINAL);
        payment.setPaymentMethod(PaymentMethod.SEPAY);
        payment.setTransactionRef(transactionRef);
        payment.setVerifiedAt(verifiedAt);
        payment.setVerifiedBy(adminUser);
        payment.setNotes("Milestone #" + milestone.getMilestoneOrder() + " verified by admin");
        paymentRepository.save(payment);
    }

    private String generateMilestoneQrUrl(Long milestoneId, Long orderId, BigDecimal amount) {
        String transferContent = buildTransferContent(milestoneId, orderId);
        return UriComponentsBuilder.fromHttpUrl(SEPAY_QR_BASE)
                .queryParam("acc", bankAccount)
                .queryParam("bank", bankCode)
                .queryParam("amount", amount.longValue())
                .queryParam("des", transferContent)
                .queryParam("template", "compact")
                .build()
                .toUriString();
    }

    public String buildTransferContent(Long milestoneId, Long orderId) {
        return "MS" + milestoneId + "PAY" + orderId;
    }

    private void notifyAdminsForVerification(PaymentMilestone milestone) {
        List<User> admins = userRepository.findByRole(UserRole.ADMIN);
        for (User admin : admins) {
            userNotificationService.pushNotificationToUser(
                    admin.getId(),
                    milestone.getOrder(),
                    NotificationType.PAYMENT,
                    "Có giao dịch mới cần xác nhận",
                    "Đơn " + milestone.getOrder().getOrderNumber() + " vừa ghi nhận thanh toán mốc '"
                            + milestone.getMilestoneName() + "', vui lòng kiểm tra và xác nhận.",
                    "milestone-unverified-" + milestone.getId() + "-" + milestone.getTransactionRef());
        }
    }

    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public record MilestoneWebhookResult(
            String status,
            String message,
            Long milestoneId,
            Long orderId,
            BigDecimal amount) {
    }
}
