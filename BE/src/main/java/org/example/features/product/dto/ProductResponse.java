package org.example.features.product.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private BigDecimal price;
    private String imageUrl;

    // Dùng để UI hiển thị nhãn "CÒN HÀNG" hoặc "HẾT HÀNG"
    private Integer stockQuantity;
    private boolean isAvailable;

    // Thông tin phụ hiển thị trên thẻ card
    private String defaultMaterial;
    private String defaultSpecification;
}