package org.example.features.production.repository;

import org.example.features.production.entity.ProductionBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ProductionBatch entity
 * Ma trận sản xuất
 */
@Repository
public interface ProductionBatchRepository extends JpaRepository<ProductionBatch, Long> {

    /**
     * Find batch by code
     */
    Optional<ProductionBatch> findByBatchCode(String batchCode);

    /**
     * Find batches by export date range
     */
    @Query("SELECT pb FROM ProductionBatch pb WHERE " +
            "pb.exportDate >= :startDate AND pb.exportDate <= :endDate " +
            "ORDER BY pb.exportDate DESC")
    Page<ProductionBatch> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    /**
     * Find recent batches for quick access
     */
    List<ProductionBatch> findTop10ByOrderByCreatedAtDesc();

    /**
     * Find unpri nted batches
     */
    @Query("SELECT pb FROM ProductionBatch pb WHERE pb.printedAt IS NULL ORDER BY pb.createdAt DESC")
    List<ProductionBatch> findUnprintedBatches();
}
