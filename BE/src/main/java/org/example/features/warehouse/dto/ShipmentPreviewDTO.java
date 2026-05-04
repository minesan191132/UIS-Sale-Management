package org.example.features.warehouse.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShipmentPreviewDTO {
    private String shipmentCode;
    private String title;
    private int totalItems;
    private int totalQty;
    private List<ShipmentItem> items;

    @Data
    public static class ShipmentItem {
        private String drawingNumber;
        private String partName;
        private String specification;
        private String material;
        private int totalQty;
        private List<OrderBreakdown> orderBreakdown;
    }

    @Data
    public static class OrderBreakdown {
        private String vnnNo;
        private String deliveryDate;
        private int quantity;
    }
}
