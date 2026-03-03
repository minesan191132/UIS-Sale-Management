package org.example.features.order.dto;

import lombok.Data;
import org.example.features.order.entity.OrderStatus;

import java.math.BigDecimal;
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
    private BigDecimal totalPrice;
    private BigDecimal depositAmount;
    private String paymentQrUrl;
    private LocalDateTime paidAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
}
