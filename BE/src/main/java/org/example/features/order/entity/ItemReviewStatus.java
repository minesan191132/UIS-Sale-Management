package org.example.features.order.entity;

/**
 * Review status for individual order items
 * Admin reviews each item before quoting
 */
public enum ItemReviewStatus {
    /** Chưa kiểm tra */
    PENDING_REVIEW,

    /** Gia công được — đã nhập đơn giá */
    APPROVED,

    /** Không gia công được — đã ghi lý do */
    REJECTED,

    /** Cần trao đổi thêm với khách hàng */
    NEED_DISCUSSION
}
