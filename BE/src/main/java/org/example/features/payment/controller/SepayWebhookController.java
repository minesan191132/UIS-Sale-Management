// package org.example.features.payment.controller;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.example.features.payment.dto.SepayWebhookDTO;
// import org.example.features.payment.service.PaymentService;
// import org.example.features.payment.service.PaymentService.WebhookResult;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;

// /**
//  * SePay Webhook endpoint tại /api/sepay/webhook
//  *
//  * URL đầy đủ (devtunnel): https://s22x1q8l-8080.asse.devtunnels.ms/api/sepay/webhook
//  * Cấu hình URL này trong dashboard SePay: https://my.sepay.vn
//  *
//  * SePay sẽ POST mỗi khi có giao dịch ngân hàng vào tài khoản đã cấu hình.
//  */
// @RestController
// @RequestMapping("/api/sepay")
// @CrossOrigin("*")
// @RequiredArgsConstructor
// @Slf4j
// public class SepayWebhookController {

//     private final PaymentService paymentService;

//     @Value("${sepay.webhook.token:}")
//     private String webhookToken;

//     /**
//      * POST /api/sepay/webhook
//      *
//      * SePay gửi Authorization: Apikey {token} trong header.
//      */
//     @PostMapping("/webhook")
//     public ResponseEntity<Map<String, Object>> handleWebhook(
//             @RequestBody SepayWebhookDTO webhook,
//             @RequestHeader(value = "Authorization", required = false) String authHeader) {

//         log.info("📥 [/api/sepay/webhook] id={}, amount={}, content={}",
//                 webhook.getId(), webhook.getTransferAmount(), webhook.getContent());

//         // Xác thực token nếu đã config
//         if (!webhookToken.isBlank()) {
//             String expected = "Apikey " + webhookToken;
//             if (!expected.equals(authHeader)) {
//                 log.warn("⛔ Webhook token invalid. Expected='{}', received='{}'", expected, authHeader);
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                         .body(Map.of("success", false, "message", "Unauthorized"));
//             }
//         }

//         try {
//             WebhookResult result = paymentService.handleSepayWebhook(webhook);
//             log.info("✅ Webhook processed: status={}, message={}", result.status(), result.message());
//             return ResponseEntity.ok(Map.of(
//                     "success", true,
//                     "status", result.status(),
//                     "message", result.message()));
//         } catch (Exception e) {
//             log.error("❌ Webhook error", e);
//             // Trả 200 để SePay không retry
//             return ResponseEntity.ok(Map.of("success", false, "message", "Internal error: " + e.getMessage()));
//         }
//     }
// }
