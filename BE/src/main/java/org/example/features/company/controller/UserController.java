package org.example.features.company.controller;

import lombok.RequiredArgsConstructor;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Admin REST API for user management
 * Security: handled by SecurityConfig (/api/admin/** => ADMIN role)
 */
@RestController("adminUserController")
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    /**
     * GET /api/admin/users?search=...&role=...&companySearch=...&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "all") String role,
            @RequestParam(required = false, defaultValue = "") String companySearch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        List<User> all = userRepository.findAll();

        // Filter by role
        if (!role.equalsIgnoreCase("all")) {
            try {
                UserRole roleEnum = UserRole.valueOf(role.toUpperCase());
                all = all.stream().filter(u -> u.getRole() == roleEnum).collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {
            }
        }

        // Filter by search (email or full name)
        if (!search.isBlank()) {
            String q = search.toLowerCase().trim();
            all = all.stream().filter(u -> (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)) ||
                    (u.getFullName() != null && u.getFullName().toLowerCase().contains(q)))
                    .collect(Collectors.toList());
        }

        // Filter by company name
        if (!companySearch.isBlank()) {
            String cq = companySearch.toLowerCase().trim();
            all = all.stream().filter(u -> u.getCompany() != null &&
                    u.getCompany().getCompanyName() != null &&
                    u.getCompany().getCompanyName().toLowerCase().contains(cq))
                    .collect(Collectors.toList());
        }

        // Sort by id desc
        all.sort((a, b) -> Long.compare(
                b.getId() != null ? b.getId() : 0L,
                a.getId() != null ? a.getId() : 0L));

        // Manual pagination
        int total = all.size();
        int fromIndex = Math.min(page * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> content = all.subList(fromIndex, toIndex)
                .stream()
                .map(UserController::toMap)
                .collect(Collectors.toList());

        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;

        return ResponseEntity.ok(Map.of(
                "content", content,
                "totalElements", total,
                "totalPages", totalPages,
                "currentPage", page,
                "pageSize", size));
    }

    /**
     * PUT /api/admin/users/{id}/role
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            try {
                user.setRole(UserRole.valueOf(body.get("role").toUpperCase()));
                userRepository.save(user);
                return ResponseEntity.ok(toMap(user));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid role"));
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/admin/users/{id} - chỉ cho phép cập nhật số điện thoại
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            if (body.containsKey("phone")) {
                user.setPhone(body.get("phone"));
            }
            userRepository.save(user);
            return ResponseEntity.ok(toMap(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/admin/users/{id}/toggle-active
     * Không cho phép khóa tài khoản Admin
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            // Chặn khóa tài khoản Admin
            if (UserRole.ADMIN.equals(user.getRole()) && Boolean.TRUE.equals(user.getIsActive())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Không thể khóa tài khoản Admin."));
            }
            user.setIsActive(!Boolean.TRUE.equals(user.getIsActive()));
            userRepository.save(user);
            return ResponseEntity.ok(toMap(user));
        }).orElse(ResponseEntity.notFound().build());
    }

    private static Map<String, Object> toMap(User u) {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        map.put("id", u.getId() != null ? u.getId() : 0L);
        map.put("email", u.getEmail() != null ? u.getEmail() : "");
        map.put("username", u.getUsername() != null ? u.getUsername() : "");
        map.put("fullName", u.getFullName() != null ? u.getFullName() : "");
        map.put("phone", u.getPhone() != null ? u.getPhone() : "");
        map.put("role", u.getRole() != null ? u.getRole().name() : "CUSTOMER");
        map.put("isActive", u.getIsActive() != null ? u.getIsActive() : false);
        map.put("companyName", u.getCompany() != null && u.getCompany().getCompanyName() != null
                ? u.getCompany().getCompanyName()
                : "");
        map.put("companyTaxCode", u.getCompany() != null && u.getCompany().getTaxCode() != null
                ? u.getCompany().getTaxCode()
                : "");
        map.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
        return map;
    }
}
