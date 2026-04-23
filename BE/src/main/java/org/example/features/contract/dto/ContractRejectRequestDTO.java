package org.example.features.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContractRejectRequestDTO {

    @NotBlank(message = "reason is required")
    @Size(max = 1000, message = "reason must be <= 1000 chars")
    private String reason;
}
