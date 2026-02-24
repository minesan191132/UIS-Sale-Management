package org.example.features.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for updating existing quote
 * Admin có thể cập nhật báo giá trước khi customer accept
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuoteDTO {
    private BigDecimal basePrice;
    private BigDecimal setupCost;
    private Integer depositPercentage;
    private String notes;
    private Integer expiryDays;
}
