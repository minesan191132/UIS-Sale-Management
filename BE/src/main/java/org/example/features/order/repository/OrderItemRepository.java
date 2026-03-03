package org.example.features.order.repository;

import org.example.features.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * OrderItem Repository
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}