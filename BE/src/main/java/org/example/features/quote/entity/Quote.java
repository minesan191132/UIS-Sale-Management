package org.example.features.quote.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.example.features.order.entity.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Quote entity for order pricing
 * Báo giá cho đơn hàng gia công
 */
@Entity
@Table(name = "quotes")
@Data
public class Quote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @Column(name = "base_price", precision = 15, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "setup_cost", precision = 15, scale = 2)
    private BigDecimal setupCost = BigDecimal.ZERO;

    @Column(name = "total_price", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "deposit_required", precision = 15, scale = 2, nullable = false)
    private BigDecimal depositRequired;

    @Column(name = "deposit_percentage")
    private Integer depositPercentage = 30;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private QuoteStatus status = QuoteStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (totalPrice == null && basePrice != null) {
            totalPrice = basePrice.add(setupCost != null ? setupCost : BigDecimal.ZERO);
        }
        if (depositRequired == null && totalPrice != null) {
            depositRequired = totalPrice.multiply(BigDecimal.valueOf(depositPercentage))
                    .divide(BigDecimal.valueOf(100));
        }
    }
}
