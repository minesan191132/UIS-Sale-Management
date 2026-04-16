package org.example.features.complaint.repository;

import org.example.features.complaint.entity.OrderComplaint;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.example.features.complaint.entity.ComplaintStatus;

public interface OrderComplaintRepository extends JpaRepository<OrderComplaint, Long> {

    Optional<OrderComplaint> findByOrderIdAndUserId(Long orderId, Long userId);

    Page<OrderComplaint> findByOrder_OrderNumberContainingIgnoreCaseAndStatus(String keyword, ComplaintStatus status, Pageable pageable);
    
    Page<OrderComplaint> findByOrder_OrderNumberContainingIgnoreCase(String keyword, Pageable pageable);
    
    Page<OrderComplaint> findByStatus(ComplaintStatus status, Pageable pageable);
}
