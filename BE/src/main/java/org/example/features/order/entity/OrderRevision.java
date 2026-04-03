package org.example.features.order.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.features.company.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "order_revisions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_order_revisions_order_revision", columnNames = { "order_id", "revision_no" })
        }
)
@Data
public class OrderRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "revision_no", nullable = false)
    private Integer revisionNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private OrderRevisionSource sourceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "import_batch_id")
    private OrderImportBatch importBatch;

    @Column(name = "snapshot_json", nullable = false, columnDefinition = "TEXT")
    private String snapshotJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
