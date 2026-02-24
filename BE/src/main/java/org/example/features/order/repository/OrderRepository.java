package org.example.features.order.repository;

import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Order Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        /**
         * Find order by order number
         */
        Optional<Order> findByOrderNumber(String orderNumber);

        /**
         * Find orders by user ID
         */
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
        Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);

        /**
         * Find orders by status
         */
        Page<Order> findByStatus(OrderStatus status, Pageable pageable);

        /**
         * Find orders by company ID
         */
        @Query("SELECT o FROM Order o WHERE o.company.id = :companyId ORDER BY o.createdAt DESC")
        Page<Order> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

        /**
         * Count orders created today for generating order number
         */
        @Query("SELECT COUNT(o) FROM Order o WHERE CAST(o.createdAt AS LocalDate) = CURRENT_DATE")
        long countTodayOrders();
}
