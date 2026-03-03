package org.example.features.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for Order Item (from Excel + review data)
 */
@Data
public class OrderItemDTO {
    private Long id;
    private String itemCode;
    private String drawingNumber;
    private String itemName;
    private String specification;
    private String material;
    private Integer quantity;
    private String unit;
    private String notes;

    // Review workflow fields
    private String reviewStatus;
    private BigDecimal unitPrice;
    private String adminNote;
    private BigDecimal totalItemPrice;
}
