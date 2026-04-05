package org.example.features.order.repository;

import org.example.features.order.entity.OrderEvent;
import org.example.features.order.entity.OrderEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

    List<OrderEvent> findByOrderIdOrderByCreatedAtDescIdDesc(Long orderId);

    Optional<OrderEvent> findTopByOrderIdAndEventTypeOrderByCreatedAtDescIdDesc(Long orderId, OrderEventType eventType);
}
