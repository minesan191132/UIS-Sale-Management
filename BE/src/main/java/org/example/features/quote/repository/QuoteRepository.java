package org.example.features.quote.repository;

import org.example.features.quote.entity.Quote;
import org.example.features.quote.entity.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    /**
     * Find quote by order ID
     */
    Optional<Quote> findByOrderId(Long orderId);

    /**
     * Find quotes by status
     */
    @Query("SELECT q FROM Quote q WHERE q.status = :status ORDER BY q.createdAt DESC")
    Page<Quote> findByStatus(@Param("status") QuoteStatus status, Pageable pageable);

    /**
     * Find expired quotes (expires_at < now and status = PENDING)
     */
    @Query("SELECT q FROM Quote q WHERE q.expiresAt < CURRENT_TIMESTAMP AND q.status = 'PENDING'")
    Page<Quote> findExpiredQuotes(Pageable pageable);
}
