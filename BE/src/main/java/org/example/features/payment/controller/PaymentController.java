package org.example.features.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.payment.dto.SepayWebhookDTO;
import org.example.features.payment.service.PaymentService;
import org.example.features.payment.service.PaymentService.PaymentInfoDTO;
import org.example.features.payment.service.PaymentService.WebhookResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST API for payment management with SePay integration
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @Value("${sepay.webhook.token:}")
    private String webhookToken;

    // =====================================================
    // SEPAY WEBHOOK ENDPOINT
    // =====================================================

    /**
     * Webhook nhận từ SePay khi có giao dịch ngân hàng.
     *
     * SePay sẽ POST về URL này sau mỗi giao dịch.
     * URL phải được cấu hình trong dashboard SePay.
     *
     * POST /api/payments/sepay/webhook
     *
     * Bảo mật: SePay gửi kèm header "Authorization: Apikey {token}"
     * hoặc trong body. Chúng ta verify token này.
     */
    @PostMapping("/sepay/webhook")
    public ResponseEntity<Map<String, Object>> handleSepayWebhook(
            @RequestBody SepayWebhookDTO webhook,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        log.info("📥 SePay Webhook received: id={}, amount={}, content={}",
                webhook.getId(), webhook.getTransferAmount(), webhook.getContent());

        // Xác thực token nếu đã config
        if (!webhookToken.isBlank()) {
            String expectedAuth = "Apikey " + webhookToken;
            if (!expectedAuth.equals(authHeader)) {
                log.warn("⛔ Invalid webhook token. Expected '{}', got '{}'", expectedAuth, authHeader);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("success", false, "message", "Unauthorized"));
            }
        }

        try {
            WebhookResult result = paymentService.handleSepayWebhook(webhook);

            log.info("Webhook result: status={}, message={}", result.status(), result.message());

            // SePay yêu cầu phản hồi {"success": true} để biết đã nhận thành công
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "status", result.status(),
                    "message", result.message()));

        } catch (Exception e) {
            log.error("Error processing SePay webhook", e);
            // Vẫn trả 200 để SePay không retry liên tục
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Internal error: " + e.getMessage()));
        }
    }

    // =====================================================
    // PAYMENT INFO & QR
    // =====================================================

    /**
     * Lấy thông tin thanh toán và QR URL cho đơn hàng.
     * FE dùng endpoint này để render màn hình thanh toán.
     *
     * GET /api/payments/orders/{orderId}/qr
     */
    @GetMapping("/orders/{orderId}/qr")
    public ResponseEntity<?> getPaymentQR(@PathVariable Long orderId) {
        try {
            PaymentInfoDTO info = paymentService.getPaymentInfo(orderId);
            return ResponseEntity.ok(info);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting payment QR for order {}", orderId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Lấy tóm tắt thanh toán cho đơn hàng.
     *
     * GET /api/payments/orders/{orderId}/summary
     */
    @GetMapping("/orders/{orderId}/summary")
    public ResponseEntity<?> getPaymentSummary(@PathVariable Long orderId) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentSummary(orderId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lịch sử thanh toán của đơn hàng.
     *
     * GET /api/payments/orders/{orderId}/history
     */
    @GetMapping("/orders/{orderId}/history")
    public ResponseEntity<?> getPaymentHistory(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(orderId));
    }

    /**
     * Danh sách payment chưa xác nhận (Admin only).
     *
     * GET /api/payments/admin/unverified
     */
    @GetMapping("/admin/unverified")
    public ResponseEntity<?> getUnverifiedPayments() {
        return ResponseEntity.ok(paymentService.getUnverifiedPayments());
    }

    // =====================================================
    // DEV / TEST ONLY — Xóa khi deploy production
    // =====================================================

    /**
     * Giả lập webhook SePay để test chuyển trạng thái đơn hàng.
     * Chỉ dùng khi DEV LOCAL — không deploy lên production!
     *
     * POST /api/payments/dev/simulate-deposit/{orderId}
     *
     * Ví dụ: POST http://localhost:8080/api/payments/dev/simulate-deposit/1
     */
    @PostMapping("/dev/simulate-deposit/{orderId}")
    public ResponseEntity<?> simulateDeposit(@PathVariable Long orderId) {
        try {
            // Lấy thông tin đơn hàng để build webhook giả
            PaymentInfoDTO info = paymentService.getPaymentInfo(orderId);

            if (info == null || info.depositAmount() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Order not found or has no deposit amount"));
            }

            // Tạo webhook payload giả — đúng format SePay gửi về
            SepayWebhookDTO fakeWebhook = new SepayWebhookDTO();
            fakeWebhook.setId(System.currentTimeMillis()); // ID giả
            fakeWebhook.setGateway("MBBank");
            fakeWebhook.setTransactionDate("2025-03-05 11:00:00");
            fakeWebhook.setAccountNumber("0934103182");
            fakeWebhook.setCode("COC-" + info.orderNumber());
            fakeWebhook.setContent("COC-" + info.orderNumber() + " dat coc don hang");
            fakeWebhook.setTransferType("in");
            fakeWebhook.setTransferAmount(info.depositAmount());
            fakeWebhook.setReferenceCode("SIMULATED-" + System.currentTimeMillis());
            fakeWebhook.setDescription("[DEV] Simulated deposit for testing");

            WebhookResult result = paymentService.handleSepayWebhook(fakeWebhook);

            log.info("[DEV] Simulated deposit for order #{}: {}", orderId, result.status());

            return ResponseEntity.ok(Map.of(
                    "simulated", true,
                    "orderId", orderId,
                    "orderNumber", info.orderNumber(),
                    "amount", info.depositAmount(),
                    "webhookStatus", result.status(),
                    "message", result.message()));

        } catch (Exception e) {
            log.error("[DEV] Simulate deposit failed for order {}", orderId, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
