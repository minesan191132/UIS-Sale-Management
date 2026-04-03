package org.example.features.order.controller;

import org.example.config.security.CustomUserDetails;
import org.example.features.auth.service.EmailService;
import org.example.features.complaint.service.OrderComplaintService;
import org.example.features.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderControllerImportValidationTest {

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
    void uploadOrder_shouldReturnMissingHeadersType_whenServiceThrowsHeaderValidation() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes());

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(100L);
        when(orderService.importOrderFromExcel(any(), eq(100L)))
                .thenThrow(new IllegalArgumentException("Missing required headers: Item Code"));

        ResponseEntity<?> response = orderController.uploadOrder(file, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("MISSING_HEADERS", payload.get("type"));
        assertEquals("Thiếu cột bắt buộc trong file Excel", payload.get("error"));
        assertInstanceOf(java.util.List.class, payload.get("missingHeaders"));
        @SuppressWarnings("unchecked")
        java.util.List<String> missingHeaders = (java.util.List<String>) payload.get("missingHeaders");
        assertEquals(java.util.List.of("Item Code"), missingHeaders);
    }

    @Test
    void adminImportOrder_shouldReturnNoValidRowsType_whenServiceThrowsNoValidItems() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes());

        CustomUserDetails admin = mock(CustomUserDetails.class);
        when(admin.getRole()).thenReturn("ADMIN");
        when(orderService.importOrderFromExcelForCompany(any(), eq(7L)))
                .thenThrow(new IllegalArgumentException("No valid items found in Excel file"));

        ResponseEntity<?> response = orderController.adminImportOrder(file, 7L, admin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("NO_VALID_ROWS", payload.get("type"));
        assertEquals("Không tìm thấy dòng dữ liệu hợp lệ để import", payload.get("error"));
    }

    @Test
    void uploadOrder_shouldReturnValidationErrorType_whenServiceThrowsGenericValidation() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "sample.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "dummy".getBytes());

        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        when(userDetails.getUserId()).thenReturn(101L);
        when(orderService.importOrderFromExcel(any(), eq(101L)))
                .thenThrow(new IllegalArgumentException("File header format is not recognized"));

        ResponseEntity<?> response = orderController.uploadOrder(file, userDetails);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) response.getBody();
        assertNotNull(payload);
        assertEquals("VALIDATION_ERROR", payload.get("type"));
        assertEquals("File header format is not recognized", payload.get("error"));
    }
}
