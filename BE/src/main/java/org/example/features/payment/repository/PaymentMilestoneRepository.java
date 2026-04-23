package org.example.features.payment.repository;

import org.example.features.payment.entity.MilestoneStatus;
import org.example.features.payment.entity.PaymentMilestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMilestoneRepository extends JpaRepository<PaymentMilestone, Long> {

    List<PaymentMilestone> findByOrderIdOrderByMilestoneOrderAsc(Long orderId);

    Optional<PaymentMilestone> findByOrderIdAndMilestoneOrder(Long orderId, Integer milestoneOrder);

    Optional<PaymentMilestone> findByIdAndOrderId(Long id, Long orderId);

    Optional<PaymentMilestone> findByTransactionRef(String transactionRef);

    boolean existsByOrderId(Long orderId);

    List<PaymentMilestone> findByStatusAndDueDate(MilestoneStatus status, LocalDate dueDate);

    List<PaymentMilestone> findByStatusAndDueDateBefore(MilestoneStatus status, LocalDate dueDate);

    List<PaymentMilestone> findByDueDateAndStatusIn(LocalDate dueDate, List<MilestoneStatus> statuses);
}
