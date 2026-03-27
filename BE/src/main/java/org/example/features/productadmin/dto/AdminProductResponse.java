package org.example.features.productadmin.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AdminProductResponse {
    private Long id;
    private String name;
    private String sku;
    private Long categoryId;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive;
    private String defaultMaterial;
    private String defaultSpecification;
    private String slug;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed: "Còn hàng" / "Sắp hết hàng" / "Hết hàng"
    private String status;
}
