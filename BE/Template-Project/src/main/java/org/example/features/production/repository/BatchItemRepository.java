package org.example.features.production.repository;

import org.example.features.production.entity.BatchItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for BatchItem entity
 * Line items in production matrix
 */
@Repository
public interface BatchItemRepository extends JpaRepository<BatchItem, Long> {

    /**
     * Find all items in a batch
     */
    @Query("SELECT bi FROM BatchItem bi WHERE bi.batch.id = :batchId ORDER BY bi.drawingNumber ASC")
    List<BatchItem> findByBatchId(@Param("batchId") Long batchId);

    /**
     * Find items by drawing number across all batches
     */
    List<BatchItem> findByDrawingNumber(String drawingNumber);

    /**
     * Get total quantity for a specific drawing number
     */
    @Query("SELECT SUM(bi.totalQuantity) FROM BatchItem bi WHERE bi.drawingNumber = :drawingNumber")
    Integer getTotalQuantityByDrawingNumber(@Param("drawingNumber") String drawingNumber);
}
