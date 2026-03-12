package org.example.features.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for full user profile data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {
    private Long userId;
    private String email;
    private String fullName;
    private String phone;
    private String gender;
    private Integer dobDay;
    private Integer dobMonth;
    private Integer dobYear;
    private String role;
    private Long companyId;
    private String companyName;
}
