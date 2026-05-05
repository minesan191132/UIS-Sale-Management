package org.example.features.order.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Tracks the current sequence value for auto-generated order codes.
 * Each row represents a unique (prefix_type, year) combination,
 * e.g. ("UIS-PH", 2026) → current_value = 5 means next code = UIS-PH-26-0006.
 */
@Entity
@Table(name = "order_sequence_tracker",
       uniqueConstraints = @UniqueConstraint(columnNames = {"prefix_type", "year"}))
@Data
public class OrderSequenceTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prefix_type", nullable = false, length = 10)
    private String prefixType;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "current_value", nullable = false)
    private Integer currentValue = 0;
}
