package org.example.features.order.service;

import lombok.RequiredArgsConstructor;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.UserRepository;
import org.example.features.order.dto.MonthlyRevenueDTO;
import org.example.features.order.dto.StatsDashboardDTO;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public enum Period { THIS_MONTH, LAST_MONTH, THIS_YEAR, ALL }

    private record DateRange(LocalDateTime since, LocalDateTime until) {}

    /** Only COMPLETED orders count as revenue (fully paid + delivered). */
    private static final List<OrderStatus> REVENUE_INCLUDED_STATUSES = List.of(OrderStatus.COMPLETED);

    private static final LocalDateTime FAR_FUTURE = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    private static final LocalDateTime EPOCH_START = LocalDateTime.of(2000, 1, 1, 0, 0, 0);

    // ===== DASHBOARD KPIs =====

    public StatsDashboardDTO getDashboard(Period period) {
        DateRange range = resolveDateRange(period);

        return StatsDashboardDTO.builder()
                .ordersAwaitingQuote(orderRepository.countByStatus(OrderStatus.PENDING_QUOTE))
                .ordersAwaitingPayment(orderRepository.countByStatus(OrderStatus.AWAITING_PAYMENT))
                .ordersAwaitingRemainingPayment(orderRepository.countByStatus(OrderStatus.AWAITING_REMAINING_PAYMENT))
                .revenueCustomManufacturing(orderRepository.sumActualRevenueByOrderType(
                        OrderType.CUSTOM_MANUFACTURING, REVENUE_INCLUDED_STATUSES, range.since(), range.until()))
                .revenueReadyMade(orderRepository.sumActualRevenueByOrderType(
                        OrderType.READY_MADE, REVENUE_INCLUDED_STATUSES, range.since(), range.until()))
                .totalOrdersCustomManufacturing(orderRepository.countOrdersByOrderType(
                        OrderType.CUSTOM_MANUFACTURING, EPOCH_START, FAR_FUTURE))
                .totalOrdersReadyMade(orderRepository.countOrdersByOrderType(
                        OrderType.READY_MADE, EPOCH_START, FAR_FUTURE))
                .newOrdersCustomManufacturing(orderRepository.countOrdersByOrderType(
                        OrderType.CUSTOM_MANUFACTURING, range.since(), range.until()))
                .newOrdersReadyMade(orderRepository.countOrdersByOrderType(
                        OrderType.READY_MADE, range.since(), range.until()))
                .totalCustomers(userRepository.countByRole(UserRole.CUSTOMER))
                .period(period.name())
                .build();
    }

    // ===== MONTHLY REVENUE for Line Chart =====

    /**
     * Returns 12-month revenue breakdown for a specific year (Jan–Dec).
     * Months with no completed orders are filled with BigDecimal.ZERO.
     */
    public MonthlyRevenueDTO getMonthlyRevenueByYear(int year) {
        LocalDateTime since = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime until = LocalDateTime.of(year + 1, 1, 1, 0, 0, 0);

        Map<String, BigDecimal> gcMap = toMonthMap(
                orderRepository.monthlyRevenueByOrderType(OrderType.CUSTOM_MANUFACTURING, REVENUE_INCLUDED_STATUSES, since, until));
        Map<String, BigDecimal> nvlMap = toMonthMap(
                orderRepository.monthlyRevenueByOrderType(OrderType.READY_MADE, REVENUE_INCLUDED_STATUSES, since, until));

        List<String> labels = new ArrayList<>();
        List<BigDecimal> gcValues = new ArrayList<>();
        List<BigDecimal> nvlValues = new ArrayList<>();

        // Always produce labels T1..T12 for the full year
        for (int month = 1; month <= 12; month++) {
            String key = year + "-" + String.format("%02d", month);
            labels.add("T" + month);
            gcValues.add(gcMap.getOrDefault(key, BigDecimal.ZERO));
            nvlValues.add(nvlMap.getOrDefault(key, BigDecimal.ZERO));
        }

        return MonthlyRevenueDTO.builder()
                .labels(labels)
                .customManufacturing(gcValues)
                .readyMade(nvlValues)
                .build();
    }

    // ===== HELPERS =====

    private Map<String, BigDecimal> toMonthMap(List<Object[]> rows) {
        Map<String, BigDecimal> map = new HashMap<>();
        for (Object[] row : rows) {
            int year  = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            BigDecimal revenue = (BigDecimal) row[2];
            map.put(year + "-" + String.format("%02d", month), revenue);
        }
        return map;
    }

    private DateRange resolveDateRange(Period period) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case THIS_MONTH -> new DateRange(
                    today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(), FAR_FUTURE);
            case LAST_MONTH -> {
                LocalDate first = today.minusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
                LocalDate next  = today.with(TemporalAdjusters.firstDayOfMonth());
                yield new DateRange(first.atStartOfDay(), next.atStartOfDay());
            }
            case THIS_YEAR -> new DateRange(LocalDate.of(today.getYear(), 1, 1).atStartOfDay(), FAR_FUTURE);
            case ALL -> new DateRange(EPOCH_START, FAR_FUTURE);
        };
    }
}
