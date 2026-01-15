package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "parts")
public class Product {
    @Id
    private String vnnNo;
    private String itemCode;
    private String drawingNo;
    private String partName;
    private String spec;
    private String material;
    private int quantity;
    private LocalDate deliveryDate;
}
