package org.example.features.sepay.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.payment.dto.SepayWebhookDTO;
import org.example.features.sepay.entity.SepayQrOrder;
import org.example.features.sepay.repository.SepayQrOrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service xử lý luồng thanh toán QR SePay độc lập.
 *
 * Content chuyển khoản: PAY-{orderId}  (phân biệt với COC-{orderNumber} cũ)
 * QR URL format: https://qr.sepay.vn/img?acc=...&bank=...&amount=...&des=PAY-{orderId}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SepayQrService {

    private final SepayQrOrderRepository qrOrderRepository;

    @Value("${sepay.bank.account}")
    private String bankAccount;

    @Value("${sepay.bank.code}")
    private String bankCode;

    private static final String SEPAY_QR_BASE = "https://qr.sepay.vn/img";
    private static final String PAYMENT_PREFIX = "PAY";

    // Pattern: PAY-123 hoặc PAY 123
    private static final Pattern PAY_PATTERN =
            Pattern.compile("(?i)" + PAYMENT_PREFIX + "[-\\s](\\d+)");

    // ──────────────────────────────────────────────
    // CREATE QR
    // ──────────────────────────────────────────────

    /**
     * Tạo (hoặc cập nhật) bản ghi QR order và trả về QR URL.
     *
     * @param orderId  ID đơn hàng
     * @param amount   Số tiền cần thanh toán
     * @return Map { qrUrl, orderId }
     */
    @Transactional
    public Map<String, Object> createQr(Long orderId, BigDecimal amount) {
        // Upsert: tìm bản ghi cũ pending, hoặc tạo mới
        SepayQrOrder qrOrder = qrOrderRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .filter(r -> "pending".equals(r.getStatus()))
                .orElseGet(SepayQrOrder::new);

        qrOrder.setOrderId(orderId);
        qrOrder.setAmount(amount);
        qrOrder.setStatus("pending");
        qrOrderRepository.save(qrOrder);

        String qrUrl = buildQrUrl(orderId, amount);
        log.info("Created QR for orderId={}, amount={}", orderId, amount);

        return Map.of("qrUrl", qrUrl, "orderId", orderId);
    }

    // ──────────────────────────────────────────────
    // GET STATUS
    // ──────────────────────────────────────────────

    /**
     * Lấy trạng thái thanh toán của orderId.
     *
     * @return Map { orderId, status, paidAmount? }
     */
    public Map<String, Object> getStatus(Long orderId) {
        return qrOrderRepository
                .findTopByOrderIdOrderByCreatedAtDesc(orderId)
                .map(r -> {
                    if ("paid".equals(r.getStatus())) {
                        return Map.<String, Object>of(
                                "orderId", orderId,
                                "status", "paid",
                                "paidAmount", r.getPaidAmount());
                    }
                    return Map.<String, Object>of("orderId", orderId, "status", "pending");
                })
                .orElse(Map.of("orderId", orderId, "status", "pending"));
    }

    // ──────────────────────────────────────────────
    // WEBHOOK
    // ──────────────────────────────────────────────

    /**
     * Xử lý webhook từ SePay.
     * Luôn trả về bình thường (không throw) để caller có thể trả HTTP 200.
     */
    @Transactional
    public void handleWebhook(SepayWebhookDTO webhook) {
        log.info("Webhook received: id={}, type={}, content={}",
                webhook.getId(), webhook.getTransferType(), webhook.getContent());

        // 1. Chỉ xử lý tiền vào
        if (!webhook.isMoneyIn()) {
            log.info("Skipping non-incoming transaction: {}", webhook.getId());
            return;
        }

        // 2. Chống duplicate
        if (webhook.getId() != null && qrOrderRepository.existsBySepayTransactionId(webhook.getId())) {
            log.warn("Duplicate webhook ignored: sepayTxnId={}", webhook.getId());
            return;
        }

        // 3. Parse orderId từ content hoặc code
        Long orderId = extractOrderId(webhook.getContent());
        if (orderId == null) orderId = extractOrderId(webhook.getCode());

        if (orderId == null) {
            log.warn("Cannot parse orderId from webhook content='{}', code='{}'",
                    webhook.getContent(), webhook.getCode());
            return;
        }

        final Long finalOrderId = orderId;

        // 4. Tìm bản ghi pending
        SepayQrOrder qrOrder = qrOrderRepository
                .findTopByOrderIdOrderByCreatedAtDesc(finalOrderId)
                .filter(r -> "pending".equals(r.getStatus()))
                .orElse(null);

        if (qrOrder == null) {
            log.warn("No pending QR order found for orderId={}", finalOrderId);
            return;
        }

        // 5. Cập nhật trạng thái → paid
        qrOrder.setStatus("paid");
        qrOrder.setPaidAmount(webhook.getTransferAmount());
        qrOrder.setSepayTransactionId(webhook.getId());
        qrOrder.setPaidAt(LocalDateTime.now());
        qrOrderRepository.save(qrOrder);

        log.info("✅ Payment confirmed: orderId={}, amount={}", finalOrderId, webhook.getTransferAmount());
    }

    // ──────────────────────────────────────────────
    // HELPERS
    // ──────────────────────────────────────────────

    private String buildQrUrl(Long orderId, BigDecimal amount) {
        String des = PAYMENT_PREFIX + "-" + orderId;
        return UriComponentsBuilder.fromHttpUrl(SEPAY_QR_BASE)
                .queryParam("acc", bankAccount)
                .queryParam("bank", bankCode)
                .queryParam("amount", amount.longValue())
                .queryParam("des", des)
                .queryParam("template", "compact")
                .build()
                .toUriString();
    }

    private Long extractOrderId(String text) {
        if (text == null || text.isBlank()) return null;
        Matcher m = PAY_PATTERN.matcher(text);
        if (m.find()) {
            try {
                return Long.parseLong(m.group(1));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
