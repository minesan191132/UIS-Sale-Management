package org.example.features.complaint.dto;

import lombok.Data;

@Data
public class OrderComplaintItemResponseDTO {
    private Long orderItemId;
    private String itemCode;
    private String itemName;
    private Integer orderedQuantity;
    private Integer missingQuantity;
}
