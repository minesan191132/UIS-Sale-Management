package org.example.features.order.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for admin to report / update a delayed delivery date.
 */
@Data
public class DelayDeliveryRequestDTO {
    @NotNull(message = "Ngày giao mới không được để trống")
    @Future(message = "Ngày giao mới phải là ngày trong tương lai")
    private LocalDate newDeliveryDate;

    @NotBlank(message = "Lý do trễ hẹn không được để trống")
    private String reason;
}
