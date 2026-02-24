package org.example.features.order.dto;

import lombok.Data;

/**
 * DTO for Order Item (from Excel)
 */
@Data
public class OrderItemDTO {
    private String itemCode; // Column 1: Item Code
    private String drawingNumber; // Column 2: Mã bản vẽ
    private String itemName; // Column 3: Tên vật tư (part_name)
    private String specification; // Column 4: Spec
    private String material; // Column 5: Material
    private Integer quantity; // Column 6: Số lượng
    private String unit; // Unit (optional)
    private String notes; // Column 7: Delivery Date stored here
}
