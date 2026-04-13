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

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    private String email; // Can be personal or company email

    @NotBlank(message = "Mật khẩu không được để trống")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,32}$",
            message = "Mật khẩu phải chứa ít nhất một chữ cái viết hoa, một chữ cái viết thường, một số và một ký tự đặc biệt."
    )
    private String password;

    @NotBlank(message = "Họ và tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số.")
    private String phone; // REQUIRED - personal phone number of the user

    // ==================== COMPANY INFORMATION ====================

    @NotBlank(message = "Mã số thuế không được để trống")
    @Pattern(regexp = "^[0-9]{10,13}$", message = "Mã số thuế phải có từ 10 đến 13 chữ số.")
    private String taxCode;

    @NotBlank(message = "Số điện thoại công ty không được để trống")
    @Pattern(regexp = "^0[0-9]{9}$", message = "Số điện thoại phải bắt đầu bằng số 0 và có đúng 10 chữ số.")
    private String companyPhone; // REQUIRED - company contact number

    @Email(message = "Email công ty (nếu có) phải đúng định dạng hợp lệ")
    private String companyEmail; // OPTIONAL - company contact email
}
