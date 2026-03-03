package org.example.features.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for creating a quote
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuoteDTO {
    private Long orderId;
    private BigDecimal basePrice;
    private BigDecimal setupCost;
    private Integer depositPercentage; // Optional, default 30%
    private String notes;
    private Integer expiryDays; // Optional, default 7 days
}
