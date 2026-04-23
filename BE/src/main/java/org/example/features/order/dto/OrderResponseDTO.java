package org.example.features.order.dto;

import lombok.Data;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Order Response
 */
@Data
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private Long companyId;
    private String companyName;
    private OrderStatus status;
    private OrderType orderType;
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String paymentQrUrl;
    private LocalDateTime paidAt;
    private LocalDateTime shippedAt;
    private LocalDateTime completedAt;
    private String notes;
    private String cancelReason;
    private String cancelledByRole;
    private Boolean rejectedByAdmin;
    private LocalDate deliveryDate;
    private LocalDate paymentDeadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
}
