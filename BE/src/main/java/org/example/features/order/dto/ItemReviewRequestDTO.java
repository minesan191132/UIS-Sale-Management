package org.example.features.order.dto;

import lombok.Data;
import org.example.features.order.entity.ItemReviewStatus;

import java.math.BigDecimal;

/**
 * DTO for admin to review an order item
 */
@Data
public class ItemReviewRequestDTO {
    private ItemReviewStatus reviewStatus;
    private BigDecimal unitPrice;
    private String adminNote;
}
