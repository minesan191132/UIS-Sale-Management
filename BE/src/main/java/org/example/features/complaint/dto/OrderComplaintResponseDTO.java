package org.example.features.complaint.dto;

import lombok.Data;
import org.example.features.complaint.entity.ComplaintStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderComplaintResponseDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private ComplaintStatus status;
    private org.example.features.complaint.entity.ComplaintType type;
    private String adminNote;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderComplaintItemResponseDTO> missingItems;
    private List<OrderComplaintImageResponseDTO> images;
    private List<OrderComplaintHistoryResponseDTO> history;
}
