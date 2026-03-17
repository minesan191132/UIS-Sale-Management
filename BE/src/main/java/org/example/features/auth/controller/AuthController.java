package org.example.features.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.CustomUserDetails;
import org.example.features.auth.dto.AuthResponseDTO;
import org.example.features.auth.dto.ForgotPasswordRequestDTO;
import org.example.features.auth.dto.LoginDTO;
import org.example.features.auth.dto.RegisterDTO;
import org.example.features.auth.dto.ResendEmailDTO;
import org.example.features.auth.dto.ResetPasswordDTO;
import org.example.features.auth.dto.VerifyOtpDTO;
import org.example.features.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

/**
 * Authentication Controller
 * Public endpoints for registration, verification and login
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Register new user with company tax code
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto) {
        String message = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", message));
    }

    /**
     * Verify user token from email link
     * GET /api/auth/verify/{token}
     */
    @GetMapping("/verify/{token}")
    public ResponseEntity<?> verifyEmail(@PathVariable String token) {
        String frontendLoginUrl = "http://localhost:5173";
        try {
            boolean isVerified = authService.verifyToken(token);
            if (isVerified) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(frontendLoginUrl + "/login?verified=true"))
                        .build();
            } else {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .location(URI.create(frontendLoginUrl + "/resend-verification?error=expired"))
                        .build();
            }
        } catch (Exception e) {
            log.error("Verification error", e);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(frontendLoginUrl + "/resend-verification?error=server"))
                    .build();
        }
    }

    /**
     * Resend verification email
     * POST /api/auth/resend-verification
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@Valid @RequestBody ResendEmailDTO dto) {
        String message = authService.resendVerificationEmail(dto.getEmail());
        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * Login with email and password
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user info
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of(
                "userId", userDetails.getUserId(),
                "email", userDetails.getEmail(),
                "fullName", userDetails.getFullName(),
                "companyId", userDetails.getCompanyId(),
                "role", userDetails.getRole()));
    }

    /**
     * Step 1 - Forgot password: send OTP to email
     * POST /api/auth/forgot-password
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        String message = authService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * Step 2 - Verify OTP: returns a one-time reset token
     * POST /api/auth/forgot-password/verify
     */
    @PostMapping("/forgot-password/verify")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpDTO dto) {
        String resetToken = authService.verifyOtp(dto.getEmail(), dto.getOtp());
        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }

    /**
     * Step 3 - Reset password using the one-time reset token
     * POST /api/auth/reset-password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        String message = authService.resetPassword(dto.getResetToken(), dto.getNewPassword());
        return ResponseEntity.ok(Map.of("message", message));
    }
}