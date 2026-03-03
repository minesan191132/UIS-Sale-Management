package org.example.features.order.dto; // Lưu ý package viết thường "dto"

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor // Bắt buộc có để dùng trong câu SELECT new...
@NoArgsConstructor
public class MaterialDetailDTO {
    private Long id;
    private String vnnNo; // Lấy từ bảng Order
    private String itemCode;
    private String drawingNumber;
    private String partName;
    private String specification;
    private String material; // materialType
    private Integer quantity;
    private LocalDate deliveryDate; // Lấy từ bảng Order
}