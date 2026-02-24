package org.example.features.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Order Item Entity
 * Represents individual items within an order
 */
@Entity
@Table(name = "order_items")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "part_name", length = 200) // Mapped to part_name in legacy schema
    private String itemName;

    @Column(name = "specification", length = 200) // Match legacy schema size
    private String specification;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Legacy schema fields (for compatibility)
    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Column(name = "drawing_number", length = 100) // Required in legacy schema but nullable for simple orders
    private String drawingNumber;

    @Column(name = "material_type", length = 100)
    private String materialType;
}