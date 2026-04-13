package org.example.features.order.dto;

import lombok.Data;

/**
 * DTO for updating customer/admin note on one order item.
 */
@Data
public class ItemNotesUpdateRequestDTO {
    private String notes;
}
