package org.example.features.user.dto;

import lombok.Data;

/**
 * Request DTO for updating user profile
 */
@Data
public class UserProfileDTO {
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private Integer dobDay;
    private Integer dobMonth;
    private Integer dobYear;
}
