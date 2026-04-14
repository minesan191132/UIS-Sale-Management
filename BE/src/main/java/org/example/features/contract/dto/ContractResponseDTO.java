package org.example.features.contract.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ContractResponseDTO {
    private Long id;
    private String contractNumber;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private ContractConfirmResponseDTO.ConfirmedByDTO confirmedBy;

    private CompanyInfoDTO supplierInfo;
    private CompanyInfoDTO buyerInfo;
    private OrderInfoDTO orderInfo;
    private List<ItemDTO> items;
    private List<MilestoneDTO> milestones;
    private List<String> qualityTerms;
    private List<String> cancelTerms;
    private String extraNotes;

    @Data
    public static class CompanyInfoDTO {
        private String companyName;
        private String taxCode;
        private String address;
        private String representative;
        private String email;
        private String phone;
    }

    @Data
    public static class OrderInfoDTO {
        private Long orderId;
        private String orderNumber;
        private BigDecimal totalPrice;
        private BigDecimal depositAmount;
        private LocalDate deliveryDate;
    }

    @Data
    public static class ItemDTO {
        private String itemCode;
        private String drawingNumber;
        private String itemName;
        private String specification;
        private String material;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
    }

    @Data
    public static class MilestoneDTO {
        private Integer milestoneOrder;
        private String milestoneName;
        private BigDecimal percentage;
        private BigDecimal amount;
        private String status;
        private LocalDate dueDate;
        private String description;
    }
}
