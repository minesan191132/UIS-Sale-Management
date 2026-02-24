package org.example.features.order.dto;

import lombok.Data; // Lombok tự sinh Getter, Setter, toString...
import java.time.LocalDate;

@Data
public class ExcelImportRowDTO {
    private String vnnNo;
    private String itemCode;
    private String drawingNumber;
    private String partName;
    private String specification;
    private String material;
    private Integer quantity;
    private LocalDate deliveryDate;
}