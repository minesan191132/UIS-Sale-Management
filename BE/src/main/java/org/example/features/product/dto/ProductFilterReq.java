package org.example.features.product.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductFilterReq {
    // Điều kiện lọc (có thể null nếu người dùng không chọn)
    private Long categoryId;
    private String keyword;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean inStockOnly;

    // Thuộc tính phân trang và sắp xếp (mặc định)
    private int page = 0;
    private int size = 9;
    private String sortType = "newest";
}