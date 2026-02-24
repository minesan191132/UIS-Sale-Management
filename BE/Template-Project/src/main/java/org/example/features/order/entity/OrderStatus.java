package org.example.features.order.entity;

/**
 * Order Status Enum
 * Represents the lifecycle of a customer order
 */
public enum OrderStatus {
    /**
     * Order created, waiting for admin to provide quote
     */
    PENDING_QUOTE,

    /**
     * Quote provided, waiting for customer payment
     */
    AWAITING_PAYMENT,

    /**
     * Payment received, order is being processed
     */
    PROCESSING,

    /**
     * Order completed and delivered
     */
    COMPLETED,

    /**
     * Order cancelled by admin or customer
     */
    CANCELLED
}
