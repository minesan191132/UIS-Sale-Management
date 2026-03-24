package org.example.features.productadmin.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminProductRequest {
    private String name;
    private String sku;
    private Long categoryId;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;
    private Boolean isActive = true;
    private String defaultMaterial;
    private String defaultSpecification;
    private String slug;
}
