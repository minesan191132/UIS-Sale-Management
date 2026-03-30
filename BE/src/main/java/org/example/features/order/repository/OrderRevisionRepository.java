package org.example.features.order.repository;

import org.example.features.order.entity.OrderRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRevisionRepository extends JpaRepository<OrderRevision, Long> {

    boolean existsByOrderId(Long orderId);

    List<OrderRevision> findByOrderIdOrderByRevisionNoDesc(Long orderId);

    Optional<OrderRevision> findByOrderIdAndRevisionNo(Long orderId, Integer revisionNo);
}
