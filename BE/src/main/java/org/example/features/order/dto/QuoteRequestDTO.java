package org.example.features.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Admin to set quote price
 */
@Data
public class QuoteRequestDTO {
    private BigDecimal totalPrice;
    private String notes;
    private List<QuoteItemUpdateDTO> items;

    @Data
    public static class QuoteItemUpdateDTO {
        private Long id;
        private BigDecimal unitPrice;
        private String adminNote;
    }
}
