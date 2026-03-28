package org.example.features.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.CustomUserDetails;
import org.example.features.auth.service.EmailService;
import org.example.features.complaint.dto.OrderComplaintResponseDTO;
import org.example.features.complaint.service.OrderComplaintService;
import org.example.features.order.dto.DelayDeliveryRequestDTO;
import org.example.features.order.dto.ItemReviewRequestDTO;
import org.example.features.order.dto.OrderResponseDTO;
import org.example.features.order.dto.QuoteRequestDTO;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Order Controller
 * Handles order-related endpoints for both customers and admins
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final OrderComplaintService orderComplaintService;
    private final EmailService emailService;

    /**
     * Customer: Create order directly from shopping cart
     * POST /api/orders/from-cart
     */
    @PostMapping("/from-cart")
    public ResponseEntity<?> createFromCart(
            @RequestBody org.example.features.order.dto.CartOrderRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            OrderResponseDTO order = orderService.createOrderFromCart(request, userDetails.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            log.warn("Cart order validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Cart order creation error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create order: " + e.getMessage()));
        }
    }

    /**
     * Customer: Upload Excel file to create order
     * POST /api/orders/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadOrder(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid file type. Please upload Excel file (.xlsx or .xls)"));
            }

            // Import order
            OrderResponseDTO order = orderService.importOrderFromExcel(file, userDetails.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(order);

        } catch (IllegalArgumentException e) {
            log.warn("Order upload validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(buildImportValidationError(e.getMessage()));
        } catch (Exception e) {
            log.error("Order upload error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload order: " + e.getMessage()));
        }
    }

    /**
     * Admin: Import Excel for a target company
     * POST /api/orders/admin-import
     */
    @PostMapping("/admin-import")
    public ResponseEntity<?> adminImportOrder(
            @RequestParam("file") MultipartFile file,
            @RequestParam("companyId") Long companyId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid file type. Please upload Excel file (.xlsx or .xls)"));
            }

            OrderResponseDTO order = orderService.importOrderFromExcelForCompany(file, companyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            log.warn("Admin import validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(buildImportValidationError(e.getMessage()));
        } catch (Exception e) {
            log.error("Admin import error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to import order: " + e.getMessage()));
        }
    }

    /**
     * Admin: Get pending import batches (staging table)
     * GET /api/orders/imports/pending
     */
    @GetMapping("/imports/pending")
    public ResponseEntity<?> getPendingImportBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<OrderResponseDTO> batches = orderService.getPendingImportBatches(pageable, keyword);
            return ResponseEntity.ok(batches);
        } catch (Exception e) {
            log.error("Error fetching pending import batches", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch pending import batches"));
        }
    }

    /**
     * Customer: Get my pending import batches
     * GET /api/orders/imports/my
     */
    @GetMapping("/imports/my")
    public ResponseEntity<?> getMyPendingImportBatches(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            List<OrderResponseDTO> batches = orderService.getMyPendingImportBatches(userDetails.getUserId());
            return ResponseEntity.ok(batches);
        } catch (Exception e) {
            log.error("Error fetching my pending import batches", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch pending imports"));
        }
    }

    /**
     * Get import batch detail by ID (admin or owner)
     * GET /api/orders/imports/{id}
     */
    @GetMapping("/imports/{id}")
    public ResponseEntity<?> getImportBatchById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isAdmin = "ADMIN".equals(userDetails.getRole());
            OrderResponseDTO batch = orderService.getImportBatchById(id, userDetails.getUserId(), isAdmin);
            return ResponseEntity.ok(batch);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching import batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch import batch"));
        }
    }

    /**
     * Admin: Approve import batch to create/update order in main table
     * POST /api/orders/imports/{id}/approve
     */
    @PostMapping("/imports/{id}/approve")
    public ResponseEntity<?> approveImportBatch(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }

            OrderResponseDTO order = orderService.approveImportBatch(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error approving import batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to approve import batch"));
        }
    }

    /**
     * Customer/Admin: Cancel pending import batch before approval
     * PUT /api/orders/imports/{id}/cancel
     */
    @PutMapping("/imports/{id}/cancel")
    public ResponseEntity<?> cancelImportBatch(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isAdmin = "ADMIN".equals(userDetails.getRole());
            orderService.cancelImportBatch(id, userDetails.getUserId(), isAdmin);
            return ResponseEntity.ok(Map.of("message", "Đã hủy đơn import chờ duyệt"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error cancelling import batch", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel import batch"));
        }
    }

    /**
     * Customer: Get my orders
     * GET /api/orders/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String orderType,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            OrderType parsedOrderType = OrderType.fromParam(orderType);
            Page<OrderResponseDTO> orders = orderService.getUserOrders(userDetails.getUserId(), status, parsedOrderType, pageable);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid orderType: " + orderType));
        } catch (Exception e) {
            log.error("Error fetching customer orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch orders"));
        }
    }

    /**
     * Admin: Get all orders
     * GET /api/orders
     */
    @GetMapping
    public ResponseEntity<?> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String orderType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Unauthorized"));
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            OrderType parsedOrderType = OrderType.fromParam(orderType);
            Page<OrderResponseDTO> orders = orderService.getAllOrders(pageable, keyword, status, dateFrom, dateTo, parsedOrderType);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid orderType: " + orderType));
        } catch (Exception e) {
            log.error("Error fetching all orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch orders"));
        }
    }

    /**
     * Get order by ID
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            boolean isAdmin = "ADMIN".equals(userDetails.getRole());
            OrderResponseDTO order = orderService.getOrderById(id, userDetails.getUserId(), isAdmin);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch order"));
        }
    }

    /**
     * Customer: Cancel a manufacturing order
     * PUT /api/orders/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            OrderResponseDTO order = orderService.cancelOrder(id, userDetails.getUserId());
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error cancelling order", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to cancel order"));
        }
    }

    /**
     * Customer: Confirm received item for shipping order.
     * PUT /api/orders/{id}/confirm-received
     */
    @PutMapping("/{id}/confirm-received")
    public ResponseEntity<?> confirmReceivedByCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            OrderResponseDTO order = orderService.confirmReceivedByCustomer(id, userDetails.getUserId());
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error confirming received for order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to confirm order receipt"));
        }
    }

    /**
     * Customer: Get my complaint for one order.
     * GET /api/orders/{id}/complaint/my
     */
    @GetMapping("/{id}/complaint/my")
    public ResponseEntity<?> getMyComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            OrderComplaintResponseDTO complaint = orderComplaintService
                    .getMyComplaintByOrder(id, userDetails.getUserId());
            return ResponseEntity.ok(complaint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching complaint for order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch complaint"));
        }
    }

    /**
     * Customer: Create or update complaint for a SHIPPING order.
     * POST /api/orders/{id}/complaint/my
     */
    @PostMapping(value = "/{id}/complaint/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upsertMyComplaint(
            @PathVariable Long id,
            @RequestParam String description,
            @RequestParam String missingItems,
            @RequestParam(required = false) List<Long> keepImageIds,
            @RequestParam(name = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            OrderComplaintResponseDTO complaint = orderComplaintService.upsertMyComplaint(
                    id,
                    userDetails.getUserId(),
                    description,
                    missingItems,
                    keepImageIds,
                    images);
            return ResponseEntity.ok(complaint);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error upserting complaint for order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to save complaint"));
        }
    }

    private Map<String, Object> buildImportValidationError(String message) {
        String raw = message == null ? "" : message;
        if (raw.startsWith("Missing required headers:")) {
            List<String> missingHeaders = extractMissingHeaders(raw);
            return Map.of(
                    "error", "Thiếu cột bắt buộc trong file Excel",
                    "type", "MISSING_HEADERS",
                    "missingHeaders", missingHeaders,
                    "details", raw);
        }

        if (raw.contains("No valid items found")) {
            return Map.of(
                    "error", "Không tìm thấy dòng dữ liệu hợp lệ để import",
                    "type", "NO_VALID_ROWS",
                    "details", raw);
        }

        return Map.of(
                "error", raw,
                "type", "VALIDATION_ERROR");
    }

    private List<String> extractMissingHeaders(String rawMessage) {
        String prefix = "Missing required headers:";
        if (rawMessage == null || !rawMessage.startsWith(prefix)) {
            return List.of();
        }

        String payload = rawMessage.substring(prefix.length()).trim();
        if (payload.isEmpty()) {
            return List.of();
        }

        String[] tokens = payload.split(";");
        List<String> result = new ArrayList<>();

        for (String token : tokens) {
            String item = token == null ? "" : token.trim();
            if (item.isEmpty()) {
                continue;
            }

            int bracketIndex = item.indexOf('(');
            if (bracketIndex > 0) {
                item = item.substring(0, bracketIndex).trim();
            }

            if (!item.isEmpty()) {
                result.add(item);
            }
        }

        return result;
    }

    /**
     * Admin: Set quote price for order
     * PUT /api/orders/{id}/quote
     */
    @PutMapping("/{id}/quote")
    public ResponseEntity<?> setQuote(
            @PathVariable Long id,
            @Valid @RequestBody QuoteRequestDTO quoteRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            OrderResponseDTO order = orderService.setQuote(id, quoteRequest);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error setting quote", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to set quote"));
        }
    }

    /**
     * Admin: Confirm payment manually
     * POST /api/orders/{id}/payment-confirm
     */
    @PostMapping("/{id}/payment-confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        try {
            OrderResponseDTO order = orderService.confirmPayment(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error confirming payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to confirm payment"));
        }
    }

    /**
     * Admin: Update order status
     * PUT /api/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        try {
            OrderResponseDTO order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating order status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update status"));
        }
    }

    /**
     * Admin: Review an individual order item
     * PUT /api/orders/{orderId}/items/{itemId}/review
     */
    @PutMapping("/{orderId}/items/{itemId}/review")
    public ResponseEntity<?> reviewOrderItem(
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @RequestBody ItemReviewRequestDTO request) {
        try {
            OrderResponseDTO order = orderService.reviewOrderItem(orderId, itemId, request);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error reviewing order item", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to review item"));
        }
    }

    /**
     * Admin: Delay delivery — update delivery_date and notify customer
     * PUT /api/orders/{id}/delay-delivery
     */
    @PutMapping("/{id}/delay-delivery")
    public ResponseEntity<?> delayDelivery(
            @PathVariable Long id,
            @Valid @RequestBody DelayDeliveryRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            OrderResponseDTO order = orderService.delayDelivery(id, request, emailService);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error delaying delivery for order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update delivery date"));
        }
    }

    /**
     * Admin: Mark order as SHIPPING (handed to carrier)
     * PUT /api/orders/{id}/ship
     */
    @PutMapping("/{id}/ship")
    public ResponseEntity<?> shipOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            OrderResponseDTO order = orderService.shipOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error shipping order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to ship order"));
        }
    }

    /**
     * Admin: Mark order as COMPLETED (delivered successfully)
     * PUT /api/orders/{id}/complete
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            OrderResponseDTO order = orderService.completeOrder(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error completing order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to complete order"));
        }
    }

    /**
     * Admin: Mark manufacturing as finished — transitions PROCESSING → AWAITING_REMAINING_PAYMENT
     * PUT /api/orders/{id}/finish-processing
     */
    @PutMapping("/{id}/finish-processing")
    public ResponseEntity<?> finishProcessing(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (!"ADMIN".equals(userDetails.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Unauthorized"));
            }
            OrderResponseDTO order = orderService.finishProcessing(id);
            return ResponseEntity.ok(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error finishing processing for order {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to finish processing"));
        }
    }
}

