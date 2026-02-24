package org.example.features.payment.entity;

/**
 * Payment method
 * Updated: Using SePay for online payment, COD for final payment on delivery
 */
public enum PaymentMethod {
    SEPAY, // SePay gateway (for deposit and online payments)
    BANK_TRANSFER, // Chuyển khoản ngân hàng thủ công
    COD, // Cash on Delivery - Thanh toán khi giao hàng (for final payment)
    OTHER // Phương thức khác
}
