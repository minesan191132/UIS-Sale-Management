package org.example.features.order.dto;

import lombok.Data;
import org.example.features.order.entity.OrderRevisionSource;

import java.time.LocalDateTime;

@Data
public class OrderRevisionSummaryDTO {
    private Long id;
    private Integer revisionNo;
    private OrderRevisionSource sourceType;
    private Long importBatchId;
    private Long createdByUserId;
    private String createdByName;
    private LocalDateTime createdAt;
}
