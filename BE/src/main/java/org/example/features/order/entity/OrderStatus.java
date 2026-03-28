package org.example.features.order.entity;

/**
 * Order Status Enum
 * Represents the lifecycle of a customer order
 */
public enum OrderStatus {
    /**
     * Order imported from Excel, waiting for admin approval before quote review
     */
    PENDING_APPROVAL,

    /**
     * Order created, waiting for admin to provide quote
     */
    PENDING_QUOTE,

    /**
     * Quote provided, waiting for customer payment
     */
    AWAITING_PAYMENT,

    /**
     * Deposit (60%) received, order is confirmed, pending full processing
     * Đã cọc 60%, đang chờ xử lý
     */
    DEPOSITED,

    /**
     * Payment received, order is being processed
     */
    PROCESSING,

    /**
     * Payment received for READY_MADE order; awaiting delivery preparation
     */
    AWAITING_DELIVERY,

    /**
     * Order handed to shipping carrier / delivery unit
     */
    SHIPPING,

    /**
     * 7 days before delivery_date for CUSTOM_MANUFACTURING:
     * customer must pay the remaining 30%
     */
    AWAITING_REMAINING_PAYMENT,

    /**
     * Order completed and delivered
     */
    COMPLETED,

    /**
     * Order cancelled by admin or customer
     */
    CANCELLED
}
