package org.example.features.complaint.dto;

import lombok.Data;

@Data
public class ComplaintMissingItemInputDTO {
    private Long orderItemId;
    private Integer missingQuantity;
}
