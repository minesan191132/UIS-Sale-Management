package org.example.features.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.CustomUserDetails;
import org.example.features.user.dto.*;
import org.example.features.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * User Controller
 * Handles profile, password, and address endpoints for authenticated users
 * Base path: /api/user
 */
@RestController("userProfileController")
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // ──────────────────────────────────────────
    //  Profile
    // ──────────────────────────────────────────

    /**
     * Get current user's full profile
     * GET /api/user/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserProfileResponseDTO profile = userService.getProfile(userDetails.getUserId());
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể tải thông tin hồ sơ"));
        }
    }

    /**
     * Update current user's profile
     * PUT /api/user/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UserProfileDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserProfileResponseDTO updated = userService.updateProfile(userDetails.getUserId(), dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể cập nhật hồ sơ"));
        }
    }

    // ──────────────────────────────────────────
    //  Password
    // ──────────────────────────────────────────

    /**
     * Change current user's password
     * PUT /api/user/change-password
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody UserChangePasswordDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userService.changePassword(userDetails.getUserId(), dto);
            return ResponseEntity.ok(Map.of("message", "Mật khẩu đã được thay đổi thành công"));
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể đổi mật khẩu"));
        }
    }

    // ──────────────────────────────────────────
    //  Addresses
    // ──────────────────────────────────────────

    /**
     * Get all addresses for current user
     * GET /api/user/addresses
     */
    @GetMapping("/addresses")
    public ResponseEntity<?> getAddresses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<UserAddressResponseDTO> addresses = userService.getAddresses(userDetails.getUserId());
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            log.error("Error fetching addresses", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể tải danh sách địa chỉ"));
        }
    }

    /**
     * Add a new address
     * POST /api/user/addresses
     */
    @PostMapping("/addresses")
    public ResponseEntity<?> addAddress(
            @Valid @RequestBody UserAddressDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserAddressResponseDTO created = userService.addAddress(userDetails.getUserId(), dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding address", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể thêm địa chỉ"));
        }
    }

    /**
     * Update an existing address
     * PUT /api/user/addresses/{id}
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody UserAddressDTO dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserAddressResponseDTO updated = userService.updateAddress(userDetails.getUserId(), id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating address {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể cập nhật địa chỉ"));
        }
    }

    /**
     * Delete an address
     * DELETE /api/user/addresses/{id}
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            userService.deleteAddress(userDetails.getUserId(), id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting address {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể xóa địa chỉ"));
        }
    }

    /**
     * Set an address as default
     * PUT /api/user/addresses/{id}/default
     */
    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<?> setDefaultAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserAddressResponseDTO updated = userService.setDefault(userDetails.getUserId(), id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error setting default address {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể đặt địa chỉ mặc định"));
        }
    }
}
