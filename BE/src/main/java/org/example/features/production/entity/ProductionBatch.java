package org.example.features.production.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.example.features.company.entity.User;
import org.example.features.order.entity.Order;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductionBatch entity - Ma trận sản xuất
 * Aggregates multiple orders for manufacturing
 */
@Entity
@Table(name = "production_batches")
@Data
public class ProductionBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique batch code (e.g., BATCH-20260203-001)
     */
    @Column(name = "batch_code", unique = true, nullable = false, length = 50)
    private String batchCode;

    /**
     * Actual export/production date
     */
    @Column(name = "export_date")
    private LocalDate exportDate = LocalDate.now();

    /**
     * Admin who created this batch
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * Notes (e.g., "Ưu tiên cắt buổi sáng")
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    /**
     * Print tracking
     */
    @Column(name = "printed_at")
    private LocalDateTime printedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Batch items (grouped by drawing_number)
     */
    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchItem> items;

    /**
     * Orders included in this batch
     * LEGACY MAPPING - Disabled for new Order Management System
     */
    // @OneToMany(mappedBy = "productionBatch")
    // @JsonIgnore
    // private List<Order> orders;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (exportDate == null) {
            exportDate = LocalDate.now();
        }
    }
}
