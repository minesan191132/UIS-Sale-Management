package org.example.features.payment.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.features.company.entity.User;
import org.example.features.order.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_milestones")
@Data
public class PaymentMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "milestone_order", nullable = false)
    private Integer milestoneOrder;

    @Column(name = "milestone_name", nullable = false, length = 100)
    private String milestoneName;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MilestoneStatus status = MilestoneStatus.PENDING;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "payment_qr_url", length = 500)
    private String paymentQrUrl;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
