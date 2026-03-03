package org.example.features.payment.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.features.order.entity.Order;
import org.example.features.company.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment entity for tracking transactions
 * Thanh toán cho đơn hàng
 */
@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", length = 20)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "notes")
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }
}
