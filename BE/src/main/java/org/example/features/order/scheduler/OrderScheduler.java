package org.example.features.order.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.auth.service.EmailService;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.repository.OrderRepository;
import org.example.features.order.service.OrderAuditService;
import org.example.features.payment.entity.MilestoneStatus;
import org.example.features.payment.entity.PaymentMilestone;
import org.example.features.payment.repository.PaymentMilestoneRepository;
import org.example.features.productadmin.AdminProductService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;

/**
 * Scheduled tasks for order lifecycle management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private static final long AUTO_COMPLETE_AFTER_DAYS = 3;
    private static final int READ_NOTIFICATION_RETENTION_DAYS = 90;

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final UserNotificationService userNotificationService;
    private final OrderAuditService orderAuditService;
    private final PaymentMilestoneRepository paymentMilestoneRepository;
    private final AdminProductService adminProductService;

    /**
     * Runs every 1 minute.
     * Finds orders in AWAITING_PAYMENT status older than 10 minutes,
     * cancels them, restores stock, and notifies the user.
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        List<Order> expiredOrders = orderRepository.findExpiredOrders(tenMinutesAgo);

        if (expiredOrders.isEmpty()) {
            return; // Không in log để tránh spam console mỗi phút
        }

        log.info("Phát hiện {} đơn hàng quá hạn thanh toán 10 phút. Đang tiến hành hủy...", expiredOrders.size());

        for (Order order : expiredOrders) {
            try {
                OrderStatus oldStatus = order.getStatus();
                order.setStatus(OrderStatus.CANCELLED);
                order.setPaymentDeadline(null);
                order.setPaymentQrUrl(null);

                // 1. Trả lại tồn kho cho từng món trong đơn
                for (org.example.features.order.entity.OrderItem item : order.getItems()) {
                    if (item.getItemName() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                        adminProductService.restoreStock(item.getItemName(), item.getQuantity());
                    }
                }

                // 2. Lưu trạng thái đơn hàng
                orderRepository.save(order);

                // 3. Ghi log lịch sử hệ thống
                orderAuditService.recordStatusEvent(
                        order,
                        OrderEventType.STATUS_CHANGED, // Hoặc tạo thêm OrderEventType.AUTO_CANCELLED nếu có
                        oldStatus,
                        OrderStatus.CANCELLED,
                        null,
                        "SYSTEM",
                        "Tự động hủy do quá hạn thanh toán 10 phút"
                );

                // 4. Gửi thông báo cho User
                userNotificationService.pushOrderNotification(
                        order,
                        NotificationType.ORDER,
                        "Đơn hàng đã bị hủy",
                        "Đơn " + order.getOrderNumber() + " đã bị hủy tự động do quá thời gian thanh toán.",
                        "order-auto-cancelled-" + order.getId()
                );

                log.info("Đã tự động hủy đơn hàng: {} và hoàn lại tồn kho.", order.getOrderNumber());
            } catch (Exception e) {
                log.error("Lỗi khi tự động hủy đơn hàng {}: {}", order.getOrderNumber(), e.getMessage());
            }
        }
    }

    /**
     * Milestone overdue notifications every 6 hours and auto-cancel at D+1 00:00.
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * ?")
    @Transactional
    public void notifyOverdueMilestones() {
        LocalDate today = LocalDate.now();
        int hour = LocalTime.now().getHour();

        List<PaymentMilestone> dueToday = paymentMilestoneRepository.findByDueDateAndStatusIn(
                today,
                List.of(MilestoneStatus.ACTIVE, MilestoneStatus.OVERDUE));

        for (PaymentMilestone milestone : dueToday) {
            Order order = milestone.getOrder();
            if (order == null || EnumSet.of(OrderStatus.CANCELLED, OrderStatus.COMPLETED).contains(order.getStatus())) {
                continue;
            }

            if (hour == 0 && milestone.getStatus() == MilestoneStatus.ACTIVE) {
                milestone.setStatus(MilestoneStatus.OVERDUE);
                paymentMilestoneRepository.save(milestone);
            }

            String message;
            if (hour == 0) {
                message = "Hôm nay là hạn cuối thanh toán mốc '" + milestone.getMilestoneName()
                        + "' của đơn " + order.getOrderNumber()
                        + ". Đơn sẽ bị hủy nếu không thanh toán trong ngày.";
            } else if (hour == 6) {
                message = "Nhắc lần 2: Mốc '" + milestone.getMilestoneName() + "' của đơn "
                        + order.getOrderNumber() + " hết hạn hôm nay. Còn 18 tiếng.";
            } else if (hour == 12) {
                message = "Nhắc lần 3: Mốc '" + milestone.getMilestoneName() + "' của đơn "
                        + order.getOrderNumber() + " hết hạn hôm nay. Còn 12 tiếng.";
            } else {
                message = "Cảnh báo cuối: Mốc '" + milestone.getMilestoneName() + "' của đơn "
                        + order.getOrderNumber() + " sẽ bị hủy sau 6 tiếng nữa.";
            }

            userNotificationService.pushOrderNotification(
                    order,
                    NotificationType.PAYMENT,
                    "Nhắc thanh toán milestone",
                    message,
                    "milestone-overdue-" + milestone.getId() + "-" + today + "-" + hour);
        }

        if (hour != 0) {
            return;
        }

        List<PaymentMilestone> expired = paymentMilestoneRepository.findByStatusAndDueDateBefore(MilestoneStatus.OVERDUE, today);
        for (PaymentMilestone milestone : expired) {
            Order order = milestone.getOrder();
            if (order == null || EnumSet.of(OrderStatus.CANCELLED, OrderStatus.COMPLETED).contains(order.getStatus())) {
                continue;
            }

            List<PaymentMilestone> orderMilestones = paymentMilestoneRepository.findByOrderIdOrderByMilestoneOrderAsc(order.getId());
            for (PaymentMilestone byOrder : orderMilestones) {
                if (byOrder.getStatus() != MilestoneStatus.PAID) {
                    byOrder.setStatus(MilestoneStatus.CANCELLED);
                    byOrder.setPaymentQrUrl(null);
                }
            }
            paymentMilestoneRepository.saveAll(orderMilestones);

            OrderStatus previousStatus = order.getStatus();
            order.setStatus(OrderStatus.CANCELLED);
            order.setPaymentDeadline(null);
            order.setPaymentQrUrl(null);
            orderRepository.save(order);

            orderAuditService.recordStatusEvent(
                    order,
                    OrderEventType.STATUS_CHANGED,
                    previousStatus,
                    OrderStatus.CANCELLED,
                    null,
                    "SYSTEM",
                    "Tự động hủy do quá hạn thanh toán milestone qua ngày D+1");

            userNotificationService.pushOrderNotification(
                    order,
                    NotificationType.ORDER,
                    "Đơn hàng đã bị hủy",
                    "Đơn " + order.getOrderNumber() + " đã bị tự động hủy do quá hạn thanh toán milestone.",
                    "order-cancelled-overdue-milestone-" + order.getId());
        }
    }

    /**
     * Runs every day at 01:00 AM.
     * Finds CUSTOM_MANUFACTURING orders in PROCESSING status whose delivery_date is exactly 7 days away,
     * transitions them to AWAITING_REMAINING_PAYMENT, and sends a payment reminder email.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void remindRemainingPayment() {
        LocalDate targetDeliveryDate = LocalDate.now().plusDays(7);
        log.info("Running remaining-payment reminder cron for delivery_date = {}", targetDeliveryDate);

        List<Order> orders = orderRepository.findCustomManufacturingProcessingByDeliveryDate(
                OrderType.CUSTOM_MANUFACTURING,
                OrderStatus.PROCESSING,
                targetDeliveryDate);

        if (orders.isEmpty()) {
            log.info("No orders to remind for remaining payment.");
            return;
        }

        for (Order order : orders) {
            try {
                // New contract/milestone flow activates remaining payment manually at finish-processing.
                if (paymentMilestoneRepository.existsByOrderId(order.getId())) {
                    continue;
                }

                order.setStatus(OrderStatus.AWAITING_REMAINING_PAYMENT);
                orderRepository.save(order);
                orderAuditService.recordStatusEvent(
                    order,
                    OrderEventType.STATUS_CHANGED,
                    OrderStatus.PROCESSING,
                    OrderStatus.AWAITING_REMAINING_PAYMENT,
                    null,
                    "SYSTEM",
                    "Scheduler nhắc thanh toán đợt 2");

                userNotificationService.pushOrderNotification(
                    order,
                    NotificationType.ORDER,
                    "Vui lòng thanh toán đợt 2",
                    "Đơn " + order.getOrderNumber()
                        + " đã đến mốc thanh toán phần còn lại. Vui lòng hoàn tất để chuẩn bị giao hàng.",
                    "order-status-" + order.getId() + "-AWAITING_REMAINING_PAYMENT");

                // Calculate remaining 30%
                BigDecimal remaining = BigDecimal.ZERO;
                if (order.getTotalPrice() != null && order.getDepositAmount() != null) {
                    remaining = order.getTotalPrice().subtract(order.getDepositAmount());
                }

                if (order.getUser() != null && order.getUser().getEmail() != null) {
                    final BigDecimal finalRemaining = remaining;
                    emailService.sendRemainingPaymentReminder(
                            order.getUser(),
                            order.getOrderNumber(),
                            finalRemaining,
                            targetDeliveryDate);
                }

                log.info("Order {} transitioned to AWAITING_REMAINING_PAYMENT and reminder sent.",
                        order.getOrderNumber());
            } catch (Exception e) {
                log.error("Failed to process payment reminder for order {}: {}",
                        order.getOrderNumber(), e.getMessage());
            }
        }
    }

    /**
     * Runs every day at 09:00 AM.
     * D+1 and D+2 reminders in account notifications for SHIPPING orders.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    @Transactional
    public void remindShippingReceiptConfirmation() {
        LocalDateTime upperBound = LocalDateTime.now().minusDays(1);
        List<Order> shippingOrders = orderRepository
            .findByStatusAndShippedAtIsNotNullAndShippedAtLessThanEqual(OrderStatus.SHIPPING, upperBound);

        if (shippingOrders.isEmpty()) {
            log.info("No SHIPPING orders eligible for D+1/D+2 reminder.");
            return;
        }

        LocalDate today = LocalDate.now();
        for (Order order : shippingOrders) {
            try {
                if (order.getShippedAt() == null) {
                    continue;
                }

                long daysSinceShipped = ChronoUnit.DAYS.between(order.getShippedAt().toLocalDate(), today);
                if (daysSinceShipped < 1 || daysSinceShipped > 2) {
                    continue;
                }

                String title = daysSinceShipped == 1
                        ? "Nhắc xác nhận đã nhận hàng (D+1)"
                        : "Nhắc xác nhận đã nhận hàng (D+2)";

                String body = "Đơn " + order.getOrderNumber()
                        + " đang ở trạng thái giao hàng. Vui lòng xác nhận đã nhận để hoàn tất đơn.";

                String notificationKey = "order-shipping-reminder-" + order.getId() + "-d" + daysSinceShipped;
                userNotificationService.pushOrderNotification(order, NotificationType.ORDER, title, body, notificationKey);
            } catch (Exception e) {
                log.error("Failed to create shipping reminder for order {}: {}",
                        order.getOrderNumber(), e.getMessage());
            }
        }
    }

    /**
     * Runs every day at 02:00 AM.
     * Fallback auto-complete: SHIPPING orders older than 3 days are marked COMPLETED
     * when customer does not click "Đã nhận hàng".
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void autoCompleteShippingOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(AUTO_COMPLETE_AFTER_DAYS);
        List<Order> orders = orderRepository
            .findByStatusAndShippedAtIsNotNullAndShippedAtLessThanEqual(OrderStatus.SHIPPING, threshold);

        if (orders.isEmpty()) {
            log.info("No SHIPPING orders eligible for auto-complete.");
            return;
        }

        for (Order order : orders) {
            try {
                order.setStatus(OrderStatus.COMPLETED);
                order.setCompletedAt(LocalDateTime.now());
                orderRepository.save(order);
                orderAuditService.recordStatusEvent(
                    order,
                    OrderEventType.AUTO_COMPLETED,
                    OrderStatus.SHIPPING,
                    OrderStatus.COMPLETED,
                    null,
                    "SYSTEM",
                    "Tự động hoàn thành sau 3 ngày giao hàng");

                userNotificationService.pushOrderNotification(
                    order,
                    NotificationType.ORDER,
                    "Đơn hàng tự động hoàn thành",
                    "Đơn " + order.getOrderNumber()
                        + " đã được hệ thống tự động xác nhận hoàn thành sau 3 ngày giao hàng.",
                    "order-auto-completed-" + order.getId());

                log.info("Order {} auto-completed after {} days in SHIPPING.",
                        order.getOrderNumber(), AUTO_COMPLETE_AFTER_DAYS);
            } catch (Exception e) {
                log.error("Failed to auto-complete order {}: {}", order.getOrderNumber(), e.getMessage());
            }
        }
    }

    /**
     * Runs every day at 03:00 AM.
     * Cleanup old read notifications to keep per-account notification list lightweight.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupOldReadNotifications() {
        long deleted = userNotificationService.cleanupOldReadNotifications(READ_NOTIFICATION_RETENTION_DAYS);
        if (deleted > 0) {
            log.info("Cleaned up {} read notifications older than {} days.",
                    deleted, READ_NOTIFICATION_RETENTION_DAYS);
        }
    }
}
