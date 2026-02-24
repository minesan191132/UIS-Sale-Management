package org.example.features.order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for Admin to set quote price
 */
@Data
public class QuoteRequestDTO {
    private BigDecimal totalPrice;
    private String notes;
}
