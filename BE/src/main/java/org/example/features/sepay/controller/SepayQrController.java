package org.example.features.sepay.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.payment.dto.SepayWebhookDTO;
import org.example.features.sepay.service.SepayQrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * SePay QR Payment endpoints.
 *
 * POST /api/payment/create-qr    — tạo QR, trả { qrUrl, orderId }
 * GET  /api/payment/status/{id}  — polling trạng thái
 * POST /webhook/sepay            — nhận webhook từ SePay (public, no auth)
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class SepayQrController {

    private final SepayQrService sepayQrService;

    // ── 1. Tạo QR ────────────────────────────────────────

    @PostMapping("/api/payment/create-qr")
    public ResponseEntity<Map<String, Object>> createQr(@RequestBody CreateQrRequest req) {
        log.info("Create QR: orderId={}, amount={}", req.orderId(), req.amount());
        Map<String, Object> result = sepayQrService.createQr(req.orderId(), req.amount());
        return ResponseEntity.ok(result);
    }

    public record CreateQrRequest(Long orderId, BigDecimal amount) {}

    // ── 2. Kiểm tra trạng thái ───────────────────────────

    @GetMapping("/api/payment/status/{orderId}")
    public ResponseEntity<Map<String, Object>> getStatus(@PathVariable Long orderId) {
        return ResponseEntity.ok(sepayQrService.getStatus(orderId));
    }

    // ── 3. Webhook SePay ─────────────────────────────────

    /**
     * SePay POST về đây sau mỗi giao dịch.
     * Luôn trả HTTP 200 { success: true } dù có lỗi — bắt buộc để SePay không retry.
     */
    @PostMapping("/webhook/sepay")
    public ResponseEntity<Map<String, Object>> handleWebhook(@RequestBody SepayWebhookDTO webhook) {
        try {
            sepayQrService.handleWebhook(webhook);
        } catch (Exception e) {
            log.error("Webhook processing error (still returning 200)", e);
        }
        return ResponseEntity.ok(Map.of("success", true));
    }
}
