package org.example.features.catalog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.example.features.order.entity.OrderItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product entity for pre-manufactured items (Public Catalog)
 * Contains template technical specs for auto-filling order items
 */
@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Category relationship
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnore
    private Category category;

    /**
     * SKU (Stock Keeping Unit) - must match Excel Item Code
     */
    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    /**
     * Display information
     */
    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Pricing
     */
    @Column(precision = 15, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * TEMPLATE Technical Specs (for auto-fill when customer selects this product)
     * These values will be copied to order_items when order is created
     */
    @Column(name = "default_drawing_number", length = 100)
    private String defaultDrawingNumber;

    @Column(name = "default_material", length = 100)
    private String defaultMaterial;

    @Column(name = "default_specification", length = 200)
    private String defaultSpecification;

    /**
     * Media
     * If null, system will use category.defaultImageUrl
     */
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * Inventory (optional)
     */
    @Column(name = "stock_quantity")
    private Integer stockQuantity = 0;

    /**
     * Status
     */
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Order items referencing this product
     * LEGACY MAPPING - Disabled for new Order Management System
     */
    // @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    // @JsonIgnore
    // private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
