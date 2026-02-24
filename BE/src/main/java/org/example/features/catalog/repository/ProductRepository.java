package org.example.features.catalog.repository;

import org.example.features.catalog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Product entity (Public Catalog)
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by SKU
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find product by slug
     */
    Optional<Product> findBySlug(String slug);

    /**
     * Find all active products
     */
    Page<Product> findByIsActiveTrue(Pageable pageable);

    /**
     * Find products by category
     */
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    /**
     * Search products by name or SKU
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
}
