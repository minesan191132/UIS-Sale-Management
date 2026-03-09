package org.example.features.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface ProductPageRepository extends JpaRepository<Product, Long> {

    // Dùng :keyword = '' thay vì IS NULL để tránh lỗi ép kiểu của PostgreSQL
    @Query("SELECT p FROM PageProduct p WHERE p.isActive = true " +
            "AND (:categoryId IS NULL OR p.categoryId = :categoryId) " +
            "AND (:keyword = '' OR LOWER(p.name) LIKE CONCAT('%', :keyword, '%') OR LOWER(p.sku) LIKE CONCAT('%', :keyword, '%')) " +
            "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
            "AND (:inStockOnly IS NULL OR :inStockOnly = false OR p.stockQuantity > 0)")
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("inStockOnly") Boolean inStockOnly,
            Pageable pageable
    );
}