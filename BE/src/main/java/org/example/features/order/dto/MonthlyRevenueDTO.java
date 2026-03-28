package org.example.features.order.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for monthly revenue line chart data.
 * Contains parallel lists: labels (e.g. "T3/2025") and revenue values per order type.
 */
@Data
@Builder
public class MonthlyRevenueDTO {

    /** Month labels, e.g. ["T1/2025", "T2/2025", ...] */
    private List<String> labels;

    /** Revenue per month for CUSTOM_MANUFACTURING (same index as labels) */
    private List<BigDecimal> customManufacturing;

    /** Revenue per month for READY_MADE (same index as labels) */
    private List<BigDecimal> readyMade;
}
