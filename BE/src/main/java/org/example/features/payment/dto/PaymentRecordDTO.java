package org.example.features.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import org.example.features.payment.entity.PaymentMethod;
import org.example.features.payment.entity.PaymentType;

/**
 * DTO for recording payment
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordDTO {
    private Long orderId;
    private BigDecimal amount;
    private PaymentType paymentType;
    private PaymentMethod paymentMethod;
    private String transactionRef;
    private String notes;
}
