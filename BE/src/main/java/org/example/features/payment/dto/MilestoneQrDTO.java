package org.example.features.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MilestoneQrDTO {
    private Long milestoneId;
    private Long orderId;
    private String orderNumber;
    private String milestoneName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private Long daysRemaining;
    private boolean overdue;
    private String qrUrl;
    private String transferContent;
    private String bankAccount;
    private String accountName;
    private String bankName;
}
