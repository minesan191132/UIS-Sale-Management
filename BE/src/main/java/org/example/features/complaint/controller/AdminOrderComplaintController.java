package org.example.features.complaint.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.CustomUserDetails;
import org.example.features.complaint.dto.OrderComplaintResponseDTO;
import org.example.features.complaint.entity.ComplaintStatus;
import org.example.features.complaint.service.OrderComplaintService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/complaints")
@RequiredArgsConstructor
@Slf4j
public class AdminOrderComplaintController {

    private final OrderComplaintService orderComplaintService;

    @GetMapping
    public ResponseEntity<?> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ComplaintStatus status,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            Page<OrderComplaintResponseDTO> result = orderComplaintService.adminGetAllComplaints(page, size, keyword, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error fetching all complaints", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch complaints"));
        }
    }

    public static class UpdateStatusRequest {
        private ComplaintStatus status;
        private String adminNote;
        public ComplaintStatus getStatus() { return status; }
        public void setStatus(ComplaintStatus status) { this.status = status; }
        public String getAdminNote() { return adminNote; }
        public void setAdminNote(String adminNote) { this.adminNote = adminNote; }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            OrderComplaintResponseDTO result = orderComplaintService.adminUpdateComplaintStatus(id, request.getStatus(), request.getAdminNote(), userDetails.getUserId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating complaint status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update complaint"));
        }
    }
}
