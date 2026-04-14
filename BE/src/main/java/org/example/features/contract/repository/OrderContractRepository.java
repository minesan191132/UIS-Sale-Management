package org.example.features.contract.repository;

import org.example.features.contract.entity.OrderContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderContractRepository extends JpaRepository<OrderContract, Long> {

    Optional<OrderContract> findTopByOrderIdOrderByCreatedAtDesc(Long orderId);

    Optional<OrderContract> findByIdAndOrderId(Long id, Long orderId);

    @Query("SELECT COUNT(c) FROM OrderContract c WHERE c.createdAt >= :start AND c.createdAt < :end")
    long countCreatedBetween(@Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end);
}
