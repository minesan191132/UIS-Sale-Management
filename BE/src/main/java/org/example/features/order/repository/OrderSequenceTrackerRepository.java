package org.example.features.order.repository;

import jakarta.persistence.LockModeType;
import org.example.features.order.entity.OrderSequenceTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for OrderSequenceTracker.
 * Uses pessimistic write lock to guarantee thread-safe sequence generation.
 */
@Repository
public interface OrderSequenceTrackerRepository extends JpaRepository<OrderSequenceTracker, Long> {

    /**
     * Find tracker row by prefix type and year with a PESSIMISTIC_WRITE lock.
     * This prevents concurrent transactions from reading the same value,
     * ensuring unique sequential order codes.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM OrderSequenceTracker t WHERE t.prefixType = :prefixType AND t.year = :year")
    Optional<OrderSequenceTracker> findByPrefixTypeAndYearForUpdate(
            @Param("prefixType") String prefixType,
            @Param("year") int year);
}
