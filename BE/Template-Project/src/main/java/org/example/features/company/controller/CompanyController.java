package org.example.features.company.controller;

import lombok.RequiredArgsConstructor;

import org.example.features.company.entity.Company;
import org.example.features.company.entity.VerificationStatus;
import org.example.features.company.dto.CompanyRegistrationDTO;
import org.example.features.company.service.CompanyService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for company management
 */
@RestController
@RequestMapping("/api/companies")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Register a new company
     * POST /api/companies/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerCompany(@RequestBody CompanyRegistrationDTO dto) {
        try {
            Company company = companyService.registerCompany(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(company);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get company by tax code
     * GET /api/companies/by-tax-code/{taxCode}
     */
    @GetMapping("/by-tax-code/{taxCode}")
    public ResponseEntity<?> getCompanyByTaxCode(@PathVariable String taxCode) {
        return companyService.getCompanyByTaxCode(taxCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get company by ID
     * GET /api/companies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long id) {
        return companyService.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search companies
     * GET /api/companies?keyword=...&page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<Company>> searchCompanies(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Company> companies = companyService.searchCompanies(keyword, PageRequest.of(page, size));
        return ResponseEntity.ok(companies);
    }

    /**
     * Get companies pending verification (Admin only)
     * GET /api/companies/admin/pending-verification
     */
    @GetMapping("/admin/pending-verification")
    public ResponseEntity<Page<Company>> getPendingVerification(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Company> companies = companyService.getCompaniesPendingVerification(PageRequest.of(page, size));
        return ResponseEntity.ok(companies);
    }

    /**
     * Verify a company (Admin only)
     * PUT /api/companies/admin/{id}/verify
     */
    @PutMapping("/admin/{id}/verify")
    public ResponseEntity<?> verifyCompany(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            VerificationStatus status = VerificationStatus.valueOf(statusStr.toUpperCase());

            Company company = companyService.verifyCompany(id, status);
            return ResponseEntity.ok(company);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
