package org.example.features.complaint.dto;

import lombok.Data;

@Data
public class OrderComplaintImageResponseDTO {
    private Long id;
    private String imageUrl;
    private String originalFilename;
}
