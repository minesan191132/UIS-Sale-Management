package org.example.features.productadmin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminProductRepository extends JpaRepository<AdminProduct, Long> {

    @Query("SELECT p FROM AdminProduct p WHERE " +
           "(:keyword = '' OR LOWER(p.name) LIKE CONCAT('%', LOWER(:keyword), '%') OR LOWER(p.sku) LIKE CONCAT('%', LOWER(:keyword), '%')) " +
           "AND (:categoryId IS NULL OR p.categoryId = :categoryId) " +
           "AND (:status = '' " +
               "OR (:status = 'in_stock' AND p.stockQuantity > 10) " +
               "OR (:status = 'low_stock' AND p.stockQuantity > 0 AND p.stockQuantity <= 10) " +
               "OR (:status = 'out_of_stock' AND (p.stockQuantity = 0 OR p.stockQuantity IS NULL)))")
    Page<AdminProduct> findByFilters(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("status") String status,
            Pageable pageable);

    long countByIsActiveTrue();

    @Query("SELECT COUNT(p) FROM AdminProduct p WHERE p.stockQuantity > 0 AND p.stockQuantity <= 10")
    long countLowStock();

    @Query("SELECT COUNT(p) FROM AdminProduct p WHERE p.stockQuantity = 0 OR p.stockQuantity IS NULL")
    long countOutOfStock();

    AdminProduct findByNameIgnoreCase(String name);
}
