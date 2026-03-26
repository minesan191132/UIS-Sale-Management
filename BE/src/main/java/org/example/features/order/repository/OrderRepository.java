package org.example.features.order.repository;

import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

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
         * Find orders by user ID and status (for tab filtering)
         */
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.createdAt DESC")
        Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status, Pageable pageable);

        /**
         * Find orders by user ID and orderType
         */
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.orderType = :orderType ORDER BY o.createdAt DESC")
        Page<Order> findByUserIdAndOrderType(@Param("userId") Long userId, @Param("orderType") OrderType orderType, Pageable pageable);

        /**
         * Find orders by user ID, status, and orderType
         */
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status AND o.orderType = :orderType ORDER BY o.createdAt DESC")
        Page<Order> findByUserIdAndStatusAndOrderType(@Param("userId") Long userId, @Param("status") OrderStatus status, @Param("orderType") OrderType orderType, Pageable pageable);

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
         * Find all orders by company ID (no pagination - used by WarehouseService)
         */
        @Query("SELECT o FROM Order o WHERE o.company.id = :companyId ORDER BY o.createdAt DESC")
        java.util.List<Order> findByCompanyId(@Param("companyId") Long companyId);

                                /**
                                 * Admin filtered search with pagination
                                 */
                                @Query("""
                                                                                                SELECT o FROM Order o
                                                                                                WHERE (:keyword IS NULL OR :keyword = '' OR
                                                                                                                         LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                                                                                                                         LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                                                                                                                         LOWER(o.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))
                                                                                                        AND (:status IS NULL OR o.status = :status)
                                                                                                        AND (:fromDate IS NULL OR o.createdAt >= :fromDate)
                                                                                                        AND (:toDateExclusive IS NULL OR o.createdAt < :toDateExclusive)
                                                                                                """)
                                Page<Order> searchAdminOrders(
                                                                                                @Param("keyword") String keyword,
                                                                                                @Param("status") OrderStatus status,
                                                                                                @Param("fromDate") LocalDateTime fromDate,
                                                                                                @Param("toDateExclusive") LocalDateTime toDateExclusive,
                                                                                                Pageable pageable);

        /**
         * Count orders created today for generating order number
         */
        @Query("SELECT COUNT(o) FROM Order o WHERE CAST(o.createdAt AS LocalDate) = CURRENT_DATE")
        long countTodayOrders();

        /**
         * Find CUSTOM_MANUFACTURING PROCESSING orders whose delivery_date equals the target date.
         * Used by the cron job to trigger 7-day remaining payment reminders.
         */
        @Query("SELECT o FROM Order o WHERE o.orderType = :orderType AND o.status = :status AND o.deliveryDate = :targetDate")
        List<Order> findCustomManufacturingProcessingByDeliveryDate(
                @Param("orderType") OrderType orderType,
                @Param("status") OrderStatus status,
                @Param("targetDate") LocalDate targetDate);
}
