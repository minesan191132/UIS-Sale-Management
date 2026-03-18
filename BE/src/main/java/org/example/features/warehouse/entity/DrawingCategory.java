package org.example.features.warehouse.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "drawing_categories")
@Data
public class DrawingCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drawing_number", nullable = false, length = 100)
    private String drawingNumber;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "default_price", precision = 15, scale = 2)
    private BigDecimal defaultPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
