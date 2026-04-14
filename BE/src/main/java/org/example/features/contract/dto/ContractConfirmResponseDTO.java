package org.example.features.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ContractConfirmResponseDTO {
    private Long id;
    private String contractNumber;
    private String status;
    private LocalDateTime confirmedAt;
    private ConfirmedByDTO confirmedBy;
    private String confirmedIp;
    private String orderStatus;
    private ActiveMilestoneDTO activeMilestone;
    private String message;

    @Data
    public static class ConfirmedByDTO {
        private Long userId;
        private String fullName;
        private String email;
    }

    @Data
    public static class ActiveMilestoneDTO {
        private Integer milestoneOrder;
        private String milestoneName;
        private BigDecimal amount;
        private LocalDate dueDate;
        private String status;
        private String paymentQrUrl;
    }
}
