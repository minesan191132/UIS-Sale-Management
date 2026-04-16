package org.example.features.complaint.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderComplaintHistoryResponseDTO {
    private Long id;
    private Long actionByUserId;
    private String actionByUserName;
    private String actionType;
    private String oldStatus;
    private String newStatus;
    private String note;
    private LocalDateTime createdAt;
}
