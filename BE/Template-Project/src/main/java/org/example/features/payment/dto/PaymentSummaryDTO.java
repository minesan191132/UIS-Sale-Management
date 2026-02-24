package org.example.features.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for payment summary of an order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDTO {
    private Long orderId;
    private String orderCode;
    private BigDecimal totalPrice;
    private BigDecimal depositRequired;
    private BigDecimal totalPaid;
    private BigDecimal remainingBalance;
    private String status;
    private List<PaymentHistoryDTO> paymentHistory;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentHistoryDTO {
        private Long paymentId;
        private BigDecimal amount;
        private String paymentType;
        private String paymentMethod;
        private String transactionRef;
        private String createdAt;
        private String verifiedAt;
        private boolean isVerified;
    }
}
