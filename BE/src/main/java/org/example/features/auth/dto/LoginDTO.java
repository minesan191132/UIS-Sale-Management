package org.example.features.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Login DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    private String email;

    @NotBlank(message = "Password không được để trống")
    private String password;
}
