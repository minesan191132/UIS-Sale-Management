package org.example.features.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.User;
import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.example.features.order.dto.ItemReviewRequestDTO;
import org.example.features.order.dto.OrderItemDTO;
import org.example.features.order.dto.OrderResponseDTO;
import org.example.features.order.dto.QuoteRequestDTO;
import org.example.features.order.entity.ItemReviewStatus;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderItemRepository;
import org.example.features.order.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Order Service
 * Handles order creation, Excel import, and order lifecycle management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    /**
     * Import order from Excel file
     */
    @Transactional
    public OrderResponseDTO importOrderFromExcel(MultipartFile file, Long userId) {
        try {
            log.info("Importing order from Excel for user ID: {}", userId);

            // Get user and company
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Company company = user.getCompany();
            if (company == null) {
                throw new IllegalArgumentException("User must belong to a company");
            }

            // Parse Excel
            List<OrderItemDTO> items = parseExcelFile(file);
            if (items.isEmpty()) {
                throw new IllegalArgumentException("No valid items found in Excel file");
            }

            // Create order
            Order order = new Order();
            order.setOrderNumber(generateOrderNumber());
            order.setUser(user);
            order.setCompany(company);
            order.setStatus(OrderStatus.PENDING_QUOTE);

            // Add items
            for (OrderItemDTO itemDTO : items) {
                OrderItem item = new OrderItem();
                item.setItemCode(itemDTO.getItemCode());
                item.setDrawingNumber(itemDTO.getDrawingNumber());
                item.setItemName(itemDTO.getItemName());
                item.setSpecification(itemDTO.getSpecification());
                item.setMaterialType(itemDTO.getMaterial());
                item.setQuantity(itemDTO.getQuantity());
                item.setUnit(itemDTO.getUnit());
                item.setNotes(itemDTO.getNotes());
                order.addItem(item);
            }

            // Save order
            Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully: {}", savedOrder.getOrderNumber());

            return mapToDTO(savedOrder);

        } catch (IOException e) {
            log.error("Error reading Excel file", e);
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }
    }

    /**
     * Parse Excel file to extract order items
     * Expected columns: STT | item_code | drawing_number | part_name | spec |
     * material | quantity | delivery_date
     */
    private List<OrderItemDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<OrderItemDTO> items = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            int activeSheetIndex = workbook.getActiveSheetIndex();
            Sheet sheet = workbook.getSheetAt(activeSheetIndex);

            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                // Check if row has data (check STT column)
                Cell firstCell = row.getCell(0);
                if (firstCell == null || getCellValueAsString(firstCell).trim().isEmpty()) {
                    continue;
                }

                OrderItemDTO item = new OrderItemDTO();

                // Column 0: STT (skip - just for numbering)

                // Column 1: VNN_NO - store in 'unit' field for display
                item.setUnit(getCellValueAsString(row.getCell(1)));

                // Column 2: Item Code (品目コード)
                item.setItemCode(getCellValueAsString(row.getCell(2)));

                // Column 3: Drawing Number (図番)
                item.setDrawingNumber(getCellValueAsString(row.getCell(3)));

                // Column 4: Part Name (品名) - required
                String partName = getCellValueAsString(row.getCell(4));
                item.setItemName(partName);

                // Column 5: Specification (型式)
                item.setSpecification(getCellValueAsString(row.getCell(5)));

                // Column 6: Material (材質)
                item.setMaterial(getCellValueAsString(row.getCell(6)));

                // Column 7: Quantity (数量) - required
                String quantityStr = getCellValueAsString(row.getCell(7));
                try {
                    item.setQuantity(Integer.parseInt(quantityStr.replaceAll("[^0-9]", "")));
                } catch (NumberFormatException e) {
                    log.warn("Invalid quantity at row {}: {}", i + 1, quantityStr);
                    continue;
                }

                // Column 8: Delivery Date (希望納期) - store in notes for now
                String deliveryDate = getCellValueAsString(row.getCell(8));
                if (!deliveryDate.isEmpty()) {
                    item.setNotes("Ngày giao: " + deliveryDate);
                }

                // Validate required fields
                if (item.getItemName() == null || item.getItemName().trim().isEmpty()) {
                    log.warn("Skipping row {} - missing part name", i + 1);
                    continue;
                }
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    log.warn("Skipping row {} - invalid quantity", i + 1);
                    continue;
                }

                items.add(item);

                // Limit to 100 items per order
                if (items.size() >= 100) {
                    log.warn("Maximum 100 items per order reached");
                    break;
                }
            }
        }

        return items;
    }

    /**
     * Helper method to get cell value as string
     * Handles FORMULA cells by checking cached result type
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        // Get actual cell type - if it's a formula, get the cached result type
        CellType cellType = (cell.getCellType() == CellType.FORMULA)
                ? cell.getCachedFormulaResultType()
                : cell.getCellType();

        return switch (cellType) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                } else {
                    // Remove decimal if it's a whole number
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        yield String.valueOf((long) numericValue);
                    } else {
                        yield String.valueOf(numericValue);
                    }
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Generate unique order number: ORD-YYYYMMDD-XXX
     */
    private String generateOrderNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long todayCount = orderRepository.countTodayOrders();
        String sequence = String.format("%03d", todayCount + 1);
        return "ORD-" + dateStr + "-" + sequence;
    }

    /**
     * Get all orders (Admin)
     */
    public Page<OrderResponseDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToDTO);
    }

    /**
     * Get user's orders
     */
    public Page<OrderResponseDTO> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(this::mapToDTO);
    }

    /**
     * Get order by ID
     */
    public OrderResponseDTO getOrderById(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Authorization check
        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to order");
        }

        return mapToDTO(order);
    }

    /**
     * Admin reviews an individual order item
     */
    @Transactional
    public OrderResponseDTO reviewOrderItem(Long orderId, Long itemId, ItemReviewRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_QUOTE) {
            throw new IllegalStateException("Chỉ có thể review khi đơn hàng ở trạng thái CHỜ BÁO GIÁ");
        }

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in this order"));

        // Validate based on review status
        if (request.getReviewStatus() == ItemReviewStatus.APPROVED) {
            if (request.getUnitPrice() == null || request.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 khi duyệt sản phẩm");
            }
            item.setUnitPrice(request.getUnitPrice());
        } else if (request.getReviewStatus() == ItemReviewStatus.REJECTED
                || request.getReviewStatus() == ItemReviewStatus.NEED_DISCUSSION) {
            if (request.getAdminNote() == null || request.getAdminNote().trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập lý do khi từ chối hoặc cần trao đổi");
            }
            item.setUnitPrice(null);
        }

        item.setReviewStatus(request.getReviewStatus());
        item.setAdminNote(request.getAdminNote());

        orderItemRepository.save(item);
        log.info("Item {} in order {} reviewed as {}", itemId, order.getOrderNumber(), request.getReviewStatus());

        return mapToDTO(order);
    }

    /**
     * Admin sets quote price and generates QR
     * Total is auto-calculated from approved items
     */
    @Transactional
    public OrderResponseDTO setQuote(Long orderId, QuoteRequestDTO quoteRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_QUOTE) {
            throw new IllegalStateException("Order is not in PENDING_QUOTE status");
        }

        // Validate: all items must be reviewed (no PENDING_REVIEW)
        boolean hasUnreviewed = order.getItems().stream()
                .anyMatch(item -> item.getReviewStatus() == ItemReviewStatus.PENDING_REVIEW);
        if (hasUnreviewed) {
            throw new IllegalStateException("Vui lòng review tất cả sản phẩm trước khi báo giá");
        }

        // Auto-calculate total from approved items: sum(unitPrice * quantity)
        BigDecimal totalPrice = order.getItems().stream()
                .filter(item -> item.getReviewStatus() == ItemReviewStatus.APPROVED)
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Không có sản phẩm nào được duyệt để báo giá");
        }

        // Set pricing
        order.setTotalPrice(totalPrice);
        order.setDepositAmount(
                totalPrice
                        .multiply(BigDecimal.valueOf(0.7))
                        .setScale(0, RoundingMode.HALF_UP));

        // Generate demo QR URL (placeholder)
        String qrUrl = generateDemoQR(order.getOrderNumber(), order.getDepositAmount());
        order.setPaymentQrUrl(qrUrl);

        // Update status
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        if (quoteRequest.getNotes() != null) {
            order.setNotes(quoteRequest.getNotes());
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Quote set for order: {} — Total: {}", savedOrder.getOrderNumber(), totalPrice);

        return mapToDTO(savedOrder);
    }

    /**
     * Generate demo QR code (placeholder)
     */
    private String generateDemoQR(String orderNumber, BigDecimal amount) {
        return "https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=seepay://pay?order=" + orderNumber
                + "&amount=" + amount;
    }

    /**
     * Confirm payment (manual or webhook)
     */
    @Transactional
    public OrderResponseDTO confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Order is not awaiting payment");
        }

        order.setStatus(OrderStatus.PROCESSING);
        order.setPaidAt(java.time.LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Payment confirmed for order: {}", savedOrder.getOrderNumber());

        return mapToDTO(savedOrder);
    }

    /**
     * Update order status (Admin)
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        log.info("Order {} status updated to {}", savedOrder.getOrderNumber(), newStatus);
        return mapToDTO(savedOrder);
    }

    /**
     * Map Order entity to DTO
     */
    private OrderResponseDTO mapToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setUserName(order.getUser().getFullName());
        dto.setCompanyId(order.getCompany().getId());
        dto.setCompanyName(order.getCompany().getCompanyName());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDepositAmount(order.getDepositAmount());
        dto.setPaymentQrUrl(order.getPaymentQrUrl());
        dto.setPaidAt(order.getPaidAt());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        // Map items
        List<OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setItemCode(item.getItemCode());
            itemDTO.setDrawingNumber(item.getDrawingNumber());
            itemDTO.setItemName(item.getItemName());
            itemDTO.setSpecification(item.getSpecification());
            itemDTO.setMaterial(item.getMaterialType());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnit(item.getUnit());
            itemDTO.setNotes(item.getNotes());
            // Review fields
            itemDTO.setReviewStatus(item.getReviewStatus() != null ? item.getReviewStatus().name() : "PENDING_REVIEW");
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setAdminNote(item.getAdminNote());
            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                itemDTO.setTotalItemPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            return itemDTO;
        }).collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }
}
