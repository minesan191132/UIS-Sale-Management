package org.example.features.production.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * BatchItem entity - Line item in Production Matrix
 * Represents aggregated products grouped by drawing_number
 */
@Entity
@Table(name = "batch_items")
@Data
public class BatchItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent batch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    @JsonIgnore
    private ProductionBatch batch;

    /**
     * Grouping key (from order_items.drawing_number)
     * All items with same drawing_number are aggregated here
     */
    @Column(name = "drawing_number", nullable = false, length = 100)
    private String drawingNumber;

    /**
     * Aggregated technical specs (from first order_item)
     */
    @Column(name = "part_name", length = 200)
    private String partName;

    @Column(name = "material_type", length = 100)
    private String materialType;

    @Column(name = "specification", length = 200)
    private String specification;

    /**
     * Total quantity (sum of all order_items with same drawing_number)
     */
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    /**
     * Order breakdown in JSON format
     * Example: {"ORD-001": 10, "ORD-002": 5}
     * Shows which orders contribute to this batch item
     */
    @Column(name = "order_breakdown", columnDefinition = "TEXT")
    private String orderBreakdown;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
