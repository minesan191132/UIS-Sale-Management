package org.example.features.payment.repository;

import org.example.features.payment.entity.Payment;
import org.example.features.payment.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find all payments for an order
     */
    List<Payment> findByOrderId(Long orderId);

    /**
     * Find verified payments for an order
     */
    @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.verifiedAt IS NOT NULL")
    List<Payment> findVerifiedPaymentsByOrderId(@Param("orderId") Long orderId);

    /**
     * Calculate total verified payments for an order
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.order.id = :orderId AND p.verifiedAt IS NOT NULL")
    BigDecimal getTotalVerifiedPaymentsByOrder(@Param("orderId") Long orderId);

    /**
     * Find payments by type
     */
    List<Payment> findByPaymentType(PaymentType paymentType);

    /**
     * Find unverified payments (pending admin confirmation)
     */
    @Query("SELECT p FROM Payment p WHERE p.verifiedAt IS NULL ORDER BY p.createdAt DESC")
    List<Payment> findUnverifiedPayments();
}
