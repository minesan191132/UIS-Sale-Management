package org.example.features.notification.dto;

import lombok.Data;
import org.example.features.notification.entity.NotificationType;

import java.time.LocalDateTime;

@Data
public class UserNotificationResponseDTO {
    private Long id;
    private Long orderId;
    private String orderNumber;
    private NotificationType type;
    private String title;
    private String body;
    private Boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
