package org.example.features.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for address data returned to client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponseDTO {
    private Long id;
    private String fullName;
    private String phone;
    private String province;
    private String district;
    private String ward;
    private String detail;
    private Boolean isDefault;
}
