package org.example.features.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating an order directly from the shopping cart.
 * Used by POST /api/orders/from-cart
 */
@Data
public class CartOrderRequestDTO {

    private List<CartItemDTO> items;
    private ShippingInfoDTO shippingInfo;

    @Data
    public static class CartItemDTO {
        private Long productId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
        private String image;
    }

    @Data
    public static class ShippingInfoDTO {
        private String recipientName;
        private String phone;
        private String email;
        private String address;
        private String companyName;
        private String taxId;
        private String invoiceEmail;
        private String note;
        private String paymentMethod; // BANK | COD
    }
}
