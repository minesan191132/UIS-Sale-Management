package org.example.features.order.entity;

/**
 * Order Type enum
 * Phân loại đơn hàng theo nguồn gốc
 */
public enum OrderType {
    /**
     * Hàng có sẵn (Pre-manufactured products from catalog)
     * Đơn hàng mua sản phẩm đã gia công sẵn từ catalog
     */
    READY_MADE,

    /**
     * Hàng đặt gia công (Custom manufacturing orders)
     * Đơn hàng gia công theo yêu cầu khách hàng (từ Excel)
     */
    CUSTOM_MANUFACTURING,

    /**
     * Hàng hỗn hợp (Hybrid - both catalog and custom items)
     * Đơn hàng có cả sản phẩm có sẵn và gia công
     */
    HYBRID

    ;

    /**
     * Parse API/query input with backward compatibility for legacy values.
     */
    public static OrderType fromParam(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String normalized = raw.trim().toUpperCase().replace('-', '_');
        if ("CUSTOM".equals(normalized)) {
            return CUSTOM_MANUFACTURING;
        }

        return OrderType.valueOf(normalized);
    }
}
