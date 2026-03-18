package org.example.features.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "part_name", length = 200)
    private String itemName;

    @Column(name = "specification", length = 200)
    private String specification;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Column(name = "drawing_number", length = 100)
    private String drawingNumber;

    @Column(name = "material_type", length = 100)
    private String materialType;

    // Review workflow fields
    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", length = 20)
    private ItemReviewStatus reviewStatus = ItemReviewStatus.PENDING_REVIEW;

    @Column(name = "unit_price", precision = 15, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
}