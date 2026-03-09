package org.example.features.payment.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for SePay Webhook payload
 * SePay sẽ POST JSON này về endpoint /api/payments/sepay/webhook
 * khi có giao dịch vào tài khoản ngân hàng.
 *
 * Docs: https://docs.sepay.vn/webhook.html
 */
@Data
@NoArgsConstructor
public class SepayWebhookDTO {

    /** ID giao dịch nội bộ của SePay */
    private Long id;

    /** Tên cổng ngân hàng (MB, VCB, TCB...) */
    private String gateway;

    /** Thời gian giao dịch (format: yyyy-MM-dd HH:mm:ss) */
    private String transactionDate;

    /** Số tài khoản nhận tiền */
    private String accountNumber;

    /** Tài khoản phụ (nếu có) */
    private String subAccount;

    /**
     * Mã nội dung chuyển khoản (do người chuyển nhập).
     * Đây là field quan trọng nhất để match đơn hàng.
     * Ví dụ: "COC-ORD-20240115-001"
     */
    private String code;

    /** Nội dung đầy đủ của giao dịch */
    private String content;

    /**
     * Loại giao dịch:
     * "in" = tiền vào (chuyển khoản đến)
     * "out" = tiền ra
     */
    private String transferType;

    /** Số tiền giao dịch (VNĐ) */
    private BigDecimal transferAmount;

    /** Số dư lũy kế */
    private BigDecimal accumulated;

    /** Mã tham chiếu giao dịch từ ngân hàng */
    private String referenceCode;

    /** Mô tả bổ sung */
    private String description;

    /** Kiểm tra đây là giao dịch tiền vào */
    public boolean isMoneyIn() {
        return "in".equalsIgnoreCase(this.transferType);
    }
}
