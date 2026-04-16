package org.example.features.complaint.repository;

import org.example.features.complaint.entity.OrderComplaintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderComplaintHistoryRepository extends JpaRepository<OrderComplaintHistory, Long> {
    List<OrderComplaintHistory> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
}
