package org.example.features.order.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.auth.service.EmailService;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Scheduled tasks for order lifecycle management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final EmailService emailService;

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
                order.setStatus(OrderStatus.AWAITING_REMAINING_PAYMENT);
                orderRepository.save(order);

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
}
