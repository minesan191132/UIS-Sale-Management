package org.example.features.complaint.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import org.example.features.order.entity.OrderItem;

@Entity
@Table(
    name = "order_complaint_items",
    indexes = {
        @Index(name = "idx_order_complaint_items_complaint", columnList = "complaint_id"),
        @Index(name = "idx_order_complaint_items_order_item", columnList = "order_item_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_order_complaint_items_complaint_item", columnNames = {"complaint_id", "order_item_id"})
    }
)
@Data
public class OrderComplaintItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private OrderComplaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @Column(name = "missing_quantity")
    private Integer missingQuantity;

    @Column(name = "defective_quantity", nullable = false)
    private Integer defectiveQuantity = 0;

    @Column(name = "reason_note", columnDefinition = "TEXT")
    private String reasonNote;
}
