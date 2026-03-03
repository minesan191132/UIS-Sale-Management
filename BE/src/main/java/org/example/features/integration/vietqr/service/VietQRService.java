package org.example.features.integration.vietqr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.integration.vietqr.dto.CompanyInfoDTO;
import org.example.features.integration.vietqr.dto.VietQRResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * VietQR API Integration Service
 * Fetches company information by tax code
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VietQRService {

    private final RestTemplate restTemplate;

    @Value("${vietqr.api.url:https://api.vietqr.io/v2}")
    private String vietQrApiUrl;

    /**
     * Lookup company by tax code via VietQR API
     * 
     * @param taxCode Mã số thuế (10-13 digits)
     * @return Company information
     * @throws IllegalArgumentException if tax code is invalid or not found
     * @throws RuntimeException         if API call fails
     */
    public CompanyInfoDTO getCompanyByTaxCode(String taxCode) {
        validateTaxCode(taxCode);

        String url = vietQrApiUrl + "/business/" + taxCode;

        try {
            log.info("Fetching company info from VietQR for tax code: {}", taxCode);

            ResponseEntity<VietQRResponse> response = restTemplate.getForEntity(
                    url,
                    VietQRResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                VietQRResponse vietQRResponse = response.getBody();

                // Check response code
                if (!"00".equals(vietQRResponse.getCode())) {
                    throw new IllegalArgumentException(
                            "Invalid tax code or company not found: " + taxCode);
                }

                VietQRResponse.VietQRData data = vietQRResponse.getData();

                if (data == null) {
                    throw new IllegalArgumentException("No company data found for tax code: " + taxCode);
                }

                log.info("Successfully fetched company: {}", data.getName());

                return CompanyInfoDTO.builder()
                        .companyName(data.getName())
                        .taxCode(data.getTaxCode())
                        .address(data.getAddress())
                        .representative(data.getDirectorName())
                        .businessType(data.getBusinessType())
                        .phoneNumber(data.getPhoneNumber())
                        .email(data.getEmail())
                        .build();
            }

            throw new RuntimeException("Failed to fetch company info: Invalid response");

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("Tax code not found in VietQR: {}", taxCode);
                throw new IllegalArgumentException("Tax code not found: " + taxCode);
            }
            log.error("VietQR API error for tax code {}: {}", taxCode, e.getMessage());
            throw new RuntimeException("VietQR API error: " + e.getMessage(), e);

        } catch (RestClientException e) {
            log.error("Failed to connect to VietQR API: {}", e.getMessage());
            throw new RuntimeException("Cannot connect to VietQR API. Please try again later.", e);
        }
    }

    /**
     * Validate tax code format
     * Vietnamese tax code: 10-13 digits
     */
    private void validateTaxCode(String taxCode) {
        if (taxCode == null || taxCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Tax code cannot be empty");
        }

        String cleanedTaxCode = taxCode.trim();

        if (!cleanedTaxCode.matches("^[0-9]{10,13}$")) {
            throw new IllegalArgumentException(
                    "Invalid tax code format. Must be 10-13 digits. Got: " + cleanedTaxCode);
        }
    }
}
