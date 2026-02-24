package org.example.features.quote.entity;

/**
 * Quote Status enum
 * Trạng thái báo giá
 */
public enum QuoteStatus {
    /**
     * Chờ admin tạo báo giá
     */
    PENDING,

    /**
     * Khách hàng đã chấp nhận
     */
    ACCEPTED,

    /**
     * Khách hàng từ chối
     */
    REJECTED,

    /**
     * Báo giá hết hạn
     */
    EXPIRED
}
