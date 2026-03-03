package org.example.features.company.repository;

import org.example.features.company.entity.Company;
import org.example.features.company.entity.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

        /**
         * Find company by tax code (Mã số thuế)
         */
        Optional<Company> findByTaxCode(String taxCode);

        /**
         * Find companies by verification status with pagination
         */
        @Query("SELECT c FROM Company c WHERE c.verificationStatus = :status ORDER BY c.createdAt DESC")
        Page<Company> findByVerificationStatus(
                        @Param("status") VerificationStatus status,
                        Pageable pageable);

        /**
         * Search companies by name or tax code
         */
        @Query("SELECT c FROM Company c WHERE " +
                        "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(c.taxCode) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "ORDER BY c.createdAt DESC")
        Page<Company> searchCompanies(@Param("keyword") String keyword, Pageable pageable);

        /**
         * Check if tax code exists
         */
        boolean existsByTaxCode(String taxCode);
}
