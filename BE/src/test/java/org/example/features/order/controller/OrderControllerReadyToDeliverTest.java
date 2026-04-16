package org.example.features.order.controller;

import org.example.config.security.CustomUserDetails;
import org.example.features.auth.service.EmailService;
import org.example.features.complaint.service.OrderComplaintService;
import org.example.features.order.dto.OrderResponseDTO;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerReadyToDeliverTest {

    private OrderService orderService;
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);
        OrderComplaintService orderComplaintService = mock(OrderComplaintService.class);
        EmailService emailService = mock(EmailService.class);
        orderController = new OrderController(orderService, orderComplaintService, emailService);
    }

    @Test
    void readyToDeliver_shouldReturnForbidden_whenUserIsNotAdmin() {
        CustomUserDetails customer = mock(CustomUserDetails.class);
        when(customer.getRole()).thenReturn("CUSTOMER");

        ResponseEntity<?> response = orderController.readyToDeliver(1L, Map.of("deliveryDate", "2026-05-01"), customer);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("Unauthorized", payload.get("error"));
    }

    @Test
    void readyToDeliver_shouldReturnBadRequest_whenDeliveryDateMissing() {
        CustomUserDetails admin = mock(CustomUserDetails.class);
        when(admin.getRole()).thenReturn("ADMIN");

        ResponseEntity<?> response = orderController.readyToDeliver(2L, new HashMap<>(), admin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("Vui lòng chọn ngày giao hàng", payload.get("error"));
    }

    @Test
    void readyToDeliver_shouldReturnBadRequest_whenDateFormatInvalid() {
        CustomUserDetails admin = mock(CustomUserDetails.class);
        when(admin.getRole()).thenReturn("ADMIN");

        ResponseEntity<?> response = orderController.readyToDeliver(3L, Map.of("deliveryDate", "05/01/2026"), admin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("Định dạng ngày không hợp lệ", payload.get("error"));
    }

    @Test
    void readyToDeliver_shouldReturnBadRequest_whenServiceRejectsBusinessRule() {
        CustomUserDetails admin = mock(CustomUserDetails.class);
        when(admin.getRole()).thenReturn("ADMIN");

        when(orderService.setDeliveryDateAndReadyToDeliver(eq(4L), eq(LocalDate.of(2026, 5, 2))))
                .thenThrow(new IllegalStateException("Đơn hàng phải đang ở trạng thái 'Đang chuẩn bị' (PROCESSING)"));

        ResponseEntity<?> response = orderController.readyToDeliver(4L, Map.of("deliveryDate", "2026-05-02"), admin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("Đơn hàng phải đang ở trạng thái 'Đang chuẩn bị' (PROCESSING)", payload.get("error"));
    }

    @Test
    void readyToDeliver_shouldReturnOk_whenRequestValid() {
        CustomUserDetails admin = mock(CustomUserDetails.class);
        when(admin.getRole()).thenReturn("ADMIN");

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(5L);
        dto.setOrderNumber("ORD-READY-005");
        dto.setOrderType(OrderType.READY_MADE);
        dto.setStatus(OrderStatus.AWAITING_DELIVERY);
        dto.setDeliveryDate(LocalDate.of(2026, 5, 3));

        when(orderService.setDeliveryDateAndReadyToDeliver(eq(5L), eq(LocalDate.of(2026, 5, 3))))
                .thenReturn(dto);

        ResponseEntity<?> response = orderController.readyToDeliver(5L, Map.of("deliveryDate", "2026-05-03"), admin);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(OrderResponseDTO.class, response.getBody());
        OrderResponseDTO body = (OrderResponseDTO) response.getBody();
        assertNotNull(body);
        assertEquals(OrderStatus.AWAITING_DELIVERY, body.getStatus());
        assertEquals(LocalDate.of(2026, 5, 3), body.getDeliveryDate());
    }
}
