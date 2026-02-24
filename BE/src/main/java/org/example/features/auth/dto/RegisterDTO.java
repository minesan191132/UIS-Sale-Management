package org.example.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for user registration
 * Includes company contact information
 */
@Data
public class RegisterDTO {

    // ==================== USER CREDENTIALS ====================

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email; // Can be personal or company email

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    // ==================== COMPANY INFORMATION ====================

    @NotBlank(message = "Tax code is required")
    @Pattern(regexp = "^[0-9]{10,13}$", message = "Tax code must be 10-13 digits")
    private String taxCode;

    @NotBlank(message = "Company phone is required")
    @Pattern(regexp = "^0[0-9]{9,10}$", message = "Phone must start with 0 and be 10-11 digits")
    private String companyPhone; // REQUIRED - company contact number

    @Email(message = "Company email must be valid if provided")
    private String companyEmail; // OPTIONAL - company contact email
}
