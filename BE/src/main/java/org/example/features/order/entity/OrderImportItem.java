package org.example.features.order.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "order_import_items")
@Data
public class OrderImportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private OrderImportBatch batch;

    @Column(name = "item_code", length = 50)
    private String itemCode;

    @Column(name = "drawing_number", length = 100)
    private String drawingNumber;

    @Column(name = "part_name", length = 200)
    private String itemName;

    @Column(name = "specification", length = 200)
    private String specification;

    @Column(name = "material_type", length = 100)
    private String materialType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit", length = 50)
    private String unit;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;
}
