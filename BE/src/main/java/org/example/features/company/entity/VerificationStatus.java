package org.example.features.company.entity;

/**
 * Verification Status enum for Company KYC
 * Trạng thái xác minh doanh nghiệp
 */
public enum VerificationStatus {
    /**
     * Chờ xác minh
     */
    PENDING,

    /**
     * Đã xác minh - cho phép giao dịch
     */
    APPROVED,

    /**
     * Từ chối xác minh
     */
    REJECTED
}
