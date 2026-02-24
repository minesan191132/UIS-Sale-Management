package org.example.features.integration.vietqr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified Company Info DTO from VietQR
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoDTO {
    private String companyName;
    private String taxCode;
    private String address;
    private String representative;
    private String businessType;
    private String phoneNumber;
    private String email;
}
