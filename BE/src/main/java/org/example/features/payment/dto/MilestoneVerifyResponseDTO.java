package org.example.features.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MilestoneVerifyResponseDTO {
    private Long milestoneId;
    private String milestoneName;
    private String status;
    private BigDecimal paidAmount;
    private String transactionRef;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private boolean orderStatusChanged;
    private String previousOrderStatus;
    private String newOrderStatus;
    private String message;
}
