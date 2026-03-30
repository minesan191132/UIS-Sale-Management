package org.example.features.order.repository;

import org.example.features.order.entity.ImportBatchStatus;
import org.example.features.order.entity.OrderImportBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderImportBatchRepository extends JpaRepository<OrderImportBatch, Long> {

    @Query("""
            SELECT b FROM OrderImportBatch b
            WHERE b.status = :status
              AND (:keyword IS NULL OR :keyword = ''
                   OR LOWER(b.importCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(b.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(b.company.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY b.createdAt DESC
            """)
    Page<OrderImportBatch> searchByStatus(
            @Param("status") ImportBatchStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);

    Optional<OrderImportBatch> findByImportCodeAndCompanyIdAndStatus(String importCode, Long companyId, ImportBatchStatus status);

    @Query("""
            SELECT b FROM OrderImportBatch b
            WHERE LOWER(b.importCode) = LOWER(:importCode)
              AND b.company.id = :companyId
              AND b.status = :status
            """)
    Optional<OrderImportBatch> findByImportCodeIgnoreCaseAndCompanyIdAndStatus(
            @Param("importCode") String importCode,
            @Param("companyId") Long companyId,
            @Param("status") ImportBatchStatus status);

    List<OrderImportBatch> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, ImportBatchStatus status);
}
