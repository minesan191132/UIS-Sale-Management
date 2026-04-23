package org.example.features.contract.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.features.company.entity.User;
import org.example.features.order.entity.Order;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_contracts")
@Data
public class OrderContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "contract_number", nullable = false, unique = true, length = 50)
    private String contractNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 25)
    private ContractStatus status = ContractStatus.DRAFT;

    @Column(name = "quality_terms", columnDefinition = "TEXT")
    private String qualityTerms;

    @Column(name = "cancel_terms", columnDefinition = "TEXT")
    private String cancelTerms;

    @Column(name = "extra_notes", columnDefinition = "TEXT")
    private String extraNotes;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by")
    private User confirmedBy;

    @Column(name = "confirmed_ip", length = 45)
    private String confirmedIp;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
