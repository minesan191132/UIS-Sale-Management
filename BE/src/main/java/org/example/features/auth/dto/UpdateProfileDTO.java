package org.example.features.auth.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {
    private String fullName;
    private String phone;
    private String gender;
    private Integer dobDay;
    private Integer dobMonth;
    private Integer dobYear;
}
