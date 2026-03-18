package org.example.features.warehouse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarehouseItemDTO {
    private String drawingNumber;
    private String partName;
    private String specification;
    private String material;
    private int totalQty;
    private BigDecimal weight;
    private int stock;
    private int orderCount;
    private String companyName;
    private List<OrderBreakdown> orders;

    @Data
    public static class OrderBreakdown {
        private Long orderId;
        private String orderNumber;
        private int quantity;
        private LocalDateTime createdAt;
    }
}
