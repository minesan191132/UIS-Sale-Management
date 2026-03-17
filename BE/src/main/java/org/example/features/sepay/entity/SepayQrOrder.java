package org.example.features.sepay.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SepayQrOrder — standalone QR payment record.
 * Luồng: PAY-{orderId} (tách biệt với luồng COC đặt cọc cũ)
 */
@Entity
@Table(name = "sepay_qr_orders")
@Data
public class SepayQrOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    /** "pending" | "paid" */
    @Column(name = "status", nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "paid_amount", precision = 15, scale = 2)
    private BigDecimal paidAmount;

    /** SePay transaction ID — unique, dùng để chống duplicate webhook */
    @Column(name = "sepay_transaction_id", unique = true)
    private Long sepayTransactionId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
