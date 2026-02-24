package org.example.features.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.features.quote.entity.Quote;
import org.example.features.quote.entity.QuoteStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for Quote
 * Prevents circular reference with Order entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponseDTO {
    private Long id;
    private Long orderId;
    private BigDecimal basePrice;
    private BigDecimal setupCost;
    private BigDecimal totalPrice;
    private BigDecimal depositRequired;
    private Integer depositPercentage;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private QuoteStatus status;

    public static QuoteResponseDTO from(Quote quote) {
        return QuoteResponseDTO.builder()
                .id(quote.getId())
                .orderId(quote.getOrder().getId())
                .basePrice(quote.getBasePrice())
                .setupCost(quote.getSetupCost())
                .totalPrice(quote.getTotalPrice())
                .depositRequired(quote.getDepositRequired())
                .depositPercentage(quote.getDepositPercentage())
                .notes(quote.getNotes())
                .createdAt(quote.getCreatedAt())
                .expiresAt(quote.getExpiresAt())
                .status(quote.getStatus())
                .build();
    }
}
