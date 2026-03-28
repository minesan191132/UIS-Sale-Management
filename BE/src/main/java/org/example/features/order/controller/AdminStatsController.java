package org.example.features.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.features.order.dto.MonthlyRevenueDTO;
import org.example.features.order.dto.StatsDashboardDTO;
import org.example.features.order.service.AdminStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin Statistics Controller
 * All endpoints require ADMIN role (enforced via SecurityConfig: /api/admin/**).
 */
@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    /** GET /api/admin/stats/dashboard?period=THIS_MONTH|LAST_MONTH|THIS_YEAR|ALL */
    @GetMapping("/dashboard")
    public ResponseEntity<StatsDashboardDTO> getDashboard(
            @RequestParam(defaultValue = "THIS_MONTH") String period) {
        AdminStatsService.Period p;
        try {
            p = AdminStatsService.Period.valueOf(period.toUpperCase());
        } catch (IllegalArgumentException e) {
            p = AdminStatsService.Period.THIS_MONTH;
        }
        return ResponseEntity.ok(adminStatsService.getDashboard(p));
    }

    /**
     * GET /api/admin/stats/monthly-revenue?year=2026
     * Returns Jan–Dec revenue for that year (line chart).
     */
    @GetMapping("/monthly-revenue")
    public ResponseEntity<MonthlyRevenueDTO> getMonthlyRevenue(
            @RequestParam(required = false) Integer year) {
        int targetYear = (year != null) ? year : java.time.LocalDate.now().getYear();
        return ResponseEntity.ok(adminStatsService.getMonthlyRevenueByYear(targetYear));
    }
}

