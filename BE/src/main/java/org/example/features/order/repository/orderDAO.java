package org.example.features.order.repository;

import org.example.features.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface orderDAO extends JpaRepository<OrderItem, String> {
}
