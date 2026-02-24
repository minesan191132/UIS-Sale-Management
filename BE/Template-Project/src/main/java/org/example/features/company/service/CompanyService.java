package org.example.features.company.service;

import lombok.RequiredArgsConstructor;
import org.example.features.company.dto.CompanyRegistrationDTO;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.VerificationStatus;
import org.example.features.company.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for managing B2B companies
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    /**
     * Register a new company
     * 
     * @param dto Company registration data
     * @return Registered company
     * @throws IllegalArgumentException if tax code already exists
     */
    @Transactional
    public Company registerCompany(CompanyRegistrationDTO dto) {
        // 1. Check if tax code already exists
        if (companyRepository.existsByTaxCode(dto.getTaxCode())) {
            throw new IllegalArgumentException("Tax code already registered: " + dto.getTaxCode());
        }

        // 2. TODO: Call VietQR API to validate and auto-fill company info
        // For now, use manual data

        Company company = new Company();
        company.setTaxCode(dto.getTaxCode());
        company.setCompanyName(dto.getName());
        company.setAddress(dto.getAddress());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        company.setVerificationStatus(VerificationStatus.PENDING);

        // 3. Handle business license upload if provided
        if (dto.getBusinessLicenseBase64() != null && !dto.getBusinessLicenseBase64().isEmpty()) {
            String savedPath = saveBusinessLicense(dto.getTaxCode(), dto.getBusinessLicenseBase64());
            company.setBusinessLicenseUrl(savedPath);
        }

        return companyRepository.save(company);
    }

    /**
     * Verify a company (Admin action)
     */
    @Transactional
    public Company verifyCompany(Long companyId, VerificationStatus status) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + companyId));

        company.setVerificationStatus(status);
        if (status == VerificationStatus.APPROVED) {
            company.setVerifiedAt(LocalDateTime.now());
        }

        return companyRepository.save(company);
    }

    /**
     * Get companies pending verification
     */
    public Page<Company> getCompaniesPendingVerification(Pageable pageable) {
        return companyRepository.findByVerificationStatus(VerificationStatus.PENDING, pageable);
    }

    /**
     * Search companies
     */
    public Page<Company> searchCompanies(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return companyRepository.findAll(pageable);
        }
        return companyRepository.searchCompanies(keyword, pageable);
    }

    /**
     * Get company by ID
     */
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    /**
     * Get company by tax code
     */
    public Optional<Company> getCompanyByTaxCode(String taxCode) {
        return companyRepository.findByTaxCode(taxCode);
    }

    /**
     * Save business license file
     * TODO: Implement actual file storage (AWS S3, local filesystem, etc.)
     */
    private String saveBusinessLicense(String taxCode, String base64Data) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
            // TODO: Save to file storage
            // For now, just return a placeholder path
            return "/uploads/business-licenses/" + taxCode + ".pdf";
        } catch (Exception e) {
            throw new RuntimeException("Failed to save business license: " + e.getMessage());
        }
    }
}
