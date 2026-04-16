package org.example.features.complaint.dto;

import lombok.Data;

@Data
public class ComplaintItemInputDTO {
    private Long orderItemId;
    private Integer missingQuantity;
    private Integer defectiveQuantity;
    private String reasonNote;
}
