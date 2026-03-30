package org.example.features.order.dto;

import lombok.Data;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderStatus;

import java.time.LocalDateTime;

@Data
public class OrderHistoryEventDTO {
    private Long id;
    private Integer revisionNo;
    private OrderEventType eventType;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private Long actorUserId;
    private String actorName;
    private String actorRole;
    private String note;
    private LocalDateTime createdAt;
}
