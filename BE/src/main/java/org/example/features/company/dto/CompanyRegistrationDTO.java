package org.example.features.company.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for company registration request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRegistrationDTO {
    private String taxCode;
    private String name;
    private String address;
    private String phone;
    private String email;

    // Optional: Used if tax code API fails
    private String businessLicenseBase64; // Base64 encoded image
}
