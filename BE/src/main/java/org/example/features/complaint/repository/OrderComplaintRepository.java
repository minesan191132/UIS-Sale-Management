package org.example.features.complaint.repository;

import org.example.features.complaint.entity.OrderComplaint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderComplaintRepository extends JpaRepository<OrderComplaint, Long> {

    @EntityGraph(attributePaths = {"missingItems", "missingItems.orderItem", "images"})
    Optional<OrderComplaint> findByOrderIdAndUserId(Long orderId, Long userId);
}
