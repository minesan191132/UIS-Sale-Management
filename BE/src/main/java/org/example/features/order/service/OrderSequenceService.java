package org.example.features.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.order.entity.OrderSequenceTracker;
import org.example.features.order.entity.OrderType;
import org.example.features.order.repository.OrderSequenceTrackerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Thread-safe service for generating sequential order codes.
 *
 * Format:
 *   - Đơn Phôi (READY_MADE):          UIS-PH-26-0001
 *   - Đơn Gia công (CUSTOM_MANUFACTURING): UIS-GC-26-0001
 *
 * The counter resets to 0001 at the start of each calendar year.
 * Concurrency is handled via PESSIMISTIC_WRITE lock on the tracker row.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSequenceService {

    private static final String PREFIX_BLANK = "UIS-PH";
    private static final String PREFIX_CUSTOM = "UIS-GC";

    private final OrderSequenceTrackerRepository trackerRepository;

    /**
     * Generate the next unique order code for the given order type.
     * Must be called within an active transaction (caller is @Transactional).
     *
     * @param orderType the order type (READY_MADE or CUSTOM_MANUFACTURING)
     * @return e.g. "UIS-PH-26-0001"
     */
    @Transactional
    public String generateNextOrderCode(OrderType orderType) {
        String prefix = resolvePrefix(orderType);
        int currentYear = LocalDate.now().getYear();
        int yearSuffix = currentYear % 100; // 2026 → 26

        // Lock row (or create if first order of the year)
        Optional<OrderSequenceTracker> trackerOpt =
                trackerRepository.findByPrefixTypeAndYearForUpdate(prefix, currentYear);

        int nextValue;
        if (trackerOpt.isPresent()) {
            OrderSequenceTracker tracker = trackerOpt.get();
            nextValue = tracker.getCurrentValue() + 1;
            tracker.setCurrentValue(nextValue);
            trackerRepository.save(tracker);
        } else {
            // First order of this type for the year
            OrderSequenceTracker newTracker = new OrderSequenceTracker();
            newTracker.setPrefixType(prefix);
            newTracker.setYear(currentYear);
            newTracker.setCurrentValue(1);
            trackerRepository.save(newTracker);
            nextValue = 1;
        }

        String code = String.format("%s-%02d-%04d", prefix, yearSuffix, nextValue);
        log.info("Generated order code: {} (type={}, year={})", code, orderType, currentYear);
        return code;
    }

    private String resolvePrefix(OrderType orderType) {
        return switch (orderType) {
            case READY_MADE -> PREFIX_BLANK;
            case CUSTOM_MANUFACTURING -> PREFIX_CUSTOM;
            default -> PREFIX_CUSTOM; // HYBRID defaults to CUSTOM
        };
    }
}
