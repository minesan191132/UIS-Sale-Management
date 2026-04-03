package org.example.features.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO for admin statistics dashboard.
 * Provides KPI summary across order types, with period filtering support.
 */
@Data
@Builder
public class StatsDashboardDTO {

    // ---- Action items (needs attention now) ----
    /** Số đơn đang chờ admin báo giá */
    private long ordersAwaitingQuote;

    /** Số đơn đang chờ khách hàng thanh toán đặt cọc (đợt 1) */
    private long ordersAwaitingPayment;

    /** Số đơn đang chờ khách hàng thanh toán phần còn lại (đợt 2) */
    private long ordersAwaitingRemainingPayment;

    // ---- Actual revenue (excludes PENDING_QUOTE, AWAITING_PAYMENT, CANCELLED) ----
    /** Doanh thu thực thu đơn gia công */
    private BigDecimal revenueCustomManufacturing;

    /** Doanh thu thực thu đơn đặt phôi NVL */
    private BigDecimal revenueReadyMade;

    // ---- Total order counts (within selected period) ----
    /** Tổng số đơn gia công */
    private long totalOrdersCustomManufacturing;

    /** Tổng số đơn đặt phôi NVL */
    private long totalOrdersReadyMade;

    // ---- New orders this month ----
    /** Số đơn gia công mới trong tháng hiện tại */
    private long newOrdersCustomManufacturing;

    /** Số đơn đặt phôi NVL mới trong tháng hiện tại */
    private long newOrdersReadyMade;

    // ---- Customers ----
    /** Tổng số khách hàng đã đăng ký (role = CUSTOMER) */
    private long totalCustomers;

    /** Kỳ thống kê đang hiển thị */
    private String period;
}
