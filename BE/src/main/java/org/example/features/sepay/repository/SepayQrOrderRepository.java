package org.example.features.sepay.repository;

import org.example.features.sepay.entity.SepayQrOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SepayQrOrderRepository extends JpaRepository<SepayQrOrder, Long> {

    Optional<SepayQrOrder> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

    boolean existsBySepayTransactionId(Long sepayTransactionId);
}
