package org.example.features.contract.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.CustomUserDetails;
import org.example.features.contract.dto.ContractConfirmResponseDTO;
import org.example.features.contract.dto.ContractRejectRequestDTO;
import org.example.features.contract.dto.ContractResponseDTO;
import org.example.features.contract.dto.ContractTermsUpdateDTO;
import org.example.features.contract.entity.OrderContract;
import org.example.features.contract.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Slf4j
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getByOrderId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isAdmin = userDetails != null && "ADMIN".equalsIgnoreCase(userDetails.getRole());
            ContractResponseDTO contract = contractService.getContractByOrderId(
                    orderId,
                    userDetails != null ? userDetails.getUserId() : null,
                    isAdmin);
            return ResponseEntity.ok(contract);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to fetch contract by order {}", orderId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to load contract"));
        }
    }

    @PutMapping("/{contractId}/terms")
    public ResponseEntity<?> updateTerms(
            @PathVariable Long contractId,
            @Valid @RequestBody ContractTermsUpdateDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null || !"ADMIN".equalsIgnoreCase(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }

            OrderContract updated = contractService.updateTerms(contractId, request);
            ContractResponseDTO details = contractService.getContractById(contractId, userDetails.getUserId(), true);

                Map<String, Object> response = new LinkedHashMap<>();
                response.put("id", updated.getId());
                response.put("contractNumber", updated.getContractNumber());
                response.put("status", updated.getStatus().name());
                response.put("qualityTerms", details.getQualityTerms());
                response.put("cancelTerms", details.getCancelTerms());
                response.put("extraNotes", updated.getExtraNotes());
                response.put("message", "Đã cập nhật điều khoản hợp đồng.");
                return ResponseEntity.ok(response);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to update terms for contract {}", contractId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update contract terms"));
        }
    }

    @PostMapping("/{contractId}/confirm")
    public ResponseEntity<?> confirmContract(
            @PathVariable Long contractId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }
            if ("ADMIN".equalsIgnoreCase(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Admin cannot confirm contract"));
            }

            String ip = extractClientIp(request);
            ContractConfirmResponseDTO response = contractService.confirmContract(
                    contractId,
                    userDetails.getUserId(),
                    ip);
            return ResponseEntity.ok(response);
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to confirm contract {}", contractId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to confirm contract"));
        }
    }

    @PostMapping("/{contractId}/reject")
    public ResponseEntity<?> rejectContract(
            @PathVariable Long contractId,
            @Valid @RequestBody ContractRejectRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (userDetails == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
            }
            if ("ADMIN".equalsIgnoreCase(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Admin cannot reject contract"));
            }

            ContractService.ContractRejectResult result = contractService.rejectContract(
                    contractId,
                    userDetails.getUserId(),
                    request.getReason());

            return ResponseEntity.ok(Map.of(
                    "id", result.id(),
                    "contractNumber", result.contractNumber(),
                    "status", result.status(),
                    "rejectedAt", result.rejectedAt(),
                    "rejectionReason", result.rejectionReason(),
                    "orderStatus", result.orderStatus(),
                    "message", result.message()));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Failed to reject contract {}", contractId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reject contract"));
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
