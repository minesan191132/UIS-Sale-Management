package org.example.features.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MilestoneListResponseDTO {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalPrice;
    private BigDecimal totalPaid;
    private BigDecimal totalRemaining;
    private Integer progressPercent;
    private List<MilestoneItemDTO> milestones;

    @Data
    public static class MilestoneItemDTO {
        private Long id;
        private Integer milestoneOrder;
        private String milestoneName;
        private BigDecimal percentage;
        private BigDecimal amount;
        private String status;
        private LocalDate dueDate;
        private LocalDateTime paidAt;
        private BigDecimal paidAmount;
        private String transactionRef;
        private LocalDateTime verifiedAt;
        private String verifiedBy;
        private String paymentQrUrl;
    }
}
