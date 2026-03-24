package org.example.features.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.User;
import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.example.features.order.dto.CartOrderRequestDTO;
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
import org.example.features.payment.service.PaymentService;
import org.example.features.warehouse.entity.DrawingMeta;
import org.example.features.warehouse.repository.DrawingMetaRepository;
import org.example.features.warehouse.service.QuotePricingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final PaymentService paymentService;
    private final QuotePricingService quotePricingService;
    private final DrawingMetaRepository drawingMetaRepository;

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

            Order savedOrder = createOrUpdateOrderFromParsedItems(items, user, company, true, false);
            log.info("Order import completed successfully: {}", savedOrder.getOrderNumber());

            return mapToDTO(savedOrder);

        } catch (IOException e) {
            log.error("Error reading Excel file", e);
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }
    }

    /**
     * Admin import for a selected company.
     * If VNN_NO already exists, old order is replaced to match source behavior.
     */
    @Transactional
    public OrderResponseDTO importOrderFromExcelForCompany(MultipartFile file, Long companyId) {
        try {
            log.info("Admin importing order for company ID: {}", companyId);

            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new IllegalArgumentException("Company not found"));

            User companyUser = userRepository.findByCompanyId(companyId).stream()
                    .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Company has no active user to own the imported order"));

            List<OrderItemDTO> items = parseExcelFile(file);
            if (items.isEmpty()) {
                throw new IllegalArgumentException("No valid items found in Excel file");
            }

            Order savedOrder = createOrUpdateOrderFromParsedItems(items, companyUser, company, false, true);
            log.info("Admin imported order successfully: {}", savedOrder.getOrderNumber());
            return mapToDTO(savedOrder);
        } catch (IOException e) {
            log.error("Error reading Excel file", e);
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }
    }

    /**
     * Create order from shopping cart (e-commerce checkout flow).
     * - Status: AWAITING_PAYMENT
     * - depositAmount = totalPrice (100% payment, no deposit logic)
     * - SePay QR generated immediately
     */
    @Transactional
    public OrderResponseDTO createOrderFromCart(CartOrderRequestDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Company company = user.getCompany();
        if (company == null) {
            throw new IllegalArgumentException("User must belong to a company");
        }

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Build order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUser(user);
        order.setCompany(company);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        // Map cart items to order items
        for (CartOrderRequestDTO.CartItemDTO cartItem : request.getItems()) {
            OrderItem item = new OrderItem();
            item.setItemName(cartItem.getName());
            item.setQuantity(cartItem.getQuantity() != null ? cartItem.getQuantity() : 1);
            item.setUnitPrice(cartItem.getPrice());
            if (cartItem.getPrice() != null && cartItem.getQuantity() != null) {
                item.setReviewStatus(org.example.features.order.entity.ItemReviewStatus.APPROVED);
            }
            order.addItem(item);
        }

        // Calculate total = sum(price * qty)
        BigDecimal totalPrice = request.getItems().stream()
                .filter(i -> i.getPrice() != null && i.getQuantity() != null)
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        // 100% payment — depositAmount equals totalPrice
        order.setDepositAmount(totalPrice);

        // Notes from shipping info
        if (request.getShippingInfo() != null) {
            CartOrderRequestDTO.ShippingInfoDTO info = request.getShippingInfo();
            StringBuilder notes = new StringBuilder();
            if (info.getRecipientName() != null) notes.append("Người nhận: ").append(info.getRecipientName()).append("\n");
            if (info.getPhone() != null)          notes.append("SĐT: ").append(info.getPhone()).append("\n");
            if (info.getAddress() != null)        notes.append("Địa chỉ: ").append(info.getAddress()).append("\n");
            if (info.getNote() != null)           notes.append("Ghi chú: ").append(info.getNote()).append("\n");
            if (info.getPaymentMethod() != null)  notes.append("Thanh toán: ").append(info.getPaymentMethod());
            order.setNotes(notes.toString().trim());
        }

        // Generate SePay QR URL
        String qrUrl = paymentService.generateSepayQrUrl(order.getOrderNumber(), totalPrice);
        order.setPaymentQrUrl(qrUrl);

        Order saved = orderRepository.save(order);
        log.info("Created cart order: {} — total={}", saved.getOrderNumber(), totalPrice);
        return mapToDTO(saved);
    }

    private Order createOrUpdateOrderFromParsedItems(
            List<OrderItemDTO> items,
            User owner,
            Company company,
            boolean allowCustomerUpdatePendingQuote,
            boolean replaceIfDuplicateVnn) {
        String vnnNo = items.stream()
                .map(OrderItemDTO::getUnit)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElse(null);

        Order order;
        if (vnnNo != null && !vnnNo.isBlank()) {
            var existing = orderRepository.findByOrderNumber(vnnNo);
            if (existing.isPresent()) {
                Order existingOrder = existing.get();

                if (allowCustomerUpdatePendingQuote) {
                    Long existingCompanyId = existingOrder.getCompany() != null ? existingOrder.getCompany().getId() : null;
                    if (existingCompanyId == null || !existingCompanyId.equals(company.getId())) {
                        throw new IllegalArgumentException("Không có quyền cập nhật đơn hàng này");
                    }
                    if (existingOrder.getStatus() != OrderStatus.PENDING_QUOTE) {
                        throw new IllegalArgumentException(
                                "Chỉ có thể import cập nhật khi đơn hàng đang ở trạng thái CHỜ BÁO GIÁ");
                    }

                    existingOrder.getItems().clear();
                    order = existingOrder;
                    log.info("Updated existing pending-quote order by VNN_NO: {}", vnnNo);
                } else if (replaceIfDuplicateVnn) {
                    orderRepository.delete(existingOrder);
                    orderRepository.flush();
                    order = new Order();
                    log.info("Replaced existing order by VNN_NO: {}", vnnNo);
                } else {
                    throw new IllegalArgumentException("Mã đơn hàng (VNN NO) đã tồn tại: " + vnnNo);
                }
            } else {
                order = new Order();
            }
            order.setOrderNumber(vnnNo);
        } else {
            order = new Order();
            order.setOrderNumber(generateOrderNumber());
        }

        order.setUser(owner);
        order.setCompany(company);
        order.setStatus(OrderStatus.PENDING_QUOTE);
        order.setTotalPrice(null);
        order.setDepositAmount(null);
        order.setPaymentQrUrl(null);
        order.setPaidAt(null);
        order.setNotes(null);

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

            if (itemDTO.getDeliveryDate() != null && !itemDTO.getDeliveryDate().isBlank()) {
                parseFlexibleDate(itemDTO.getDeliveryDate()).ifPresentOrElse(
                        item::setDeliveryDate,
                        () -> log.warn("Cannot parse delivery date: {}", itemDTO.getDeliveryDate()));
            }
            order.addItem(item);
        }

        return orderRepository.save(order);
    }

    /**
     * Parse Excel file to extract order items
     * Expected columns: STT | item_code | drawing_number | part_name | spec |
     * material | quantity | delivery_date
     */
    private List<OrderItemDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<OrderItemDTO> items = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = findDataSheet(workbook);
            int deliveryDateColumnIndex = detectDeliveryDateColumnIndex(sheet);
            log.info("Detected delivery date column index: {}", deliveryDateColumnIndex);

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

                // Delivery Date from detected column in sheet 梱包指示
                String deliveryDate = getCellValueAsString(row.getCell(deliveryDateColumnIndex));
                if (!deliveryDate.isEmpty()) {
                    item.setDeliveryDate(deliveryDate);
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

    private int detectDeliveryDateColumnIndex(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            return 8;
        }

        int lastCellNum = Math.max(headerRow.getLastCellNum(), (short) 9);
        for (int col = 0; col < lastCellNum; col++) {
            String header = getCellValueAsString(headerRow.getCell(col));
            if (isDeliveryDateHeader(header)) {
                return col;
            }
        }

        // Legacy fallback used in existing imports.
        return 8;
    }

    private boolean isDeliveryDateHeader(String header) {
        if (header == null) {
            return false;
        }
        String normalized = header
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");

        return normalized.contains("希望納期")
                || normalized.contains("出荷日")
                || normalized.contains("納期")
                || normalized.contains("deliverydate")
                || normalized.contains("delivery")
                || normalized.contains("ngayxuat")
                || normalized.contains("ngàyxuất");
    }

    private Optional<LocalDate> parseFlexibleDate(String rawValue) {
        if (rawValue == null) {
            return Optional.empty();
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }

        // Excel-style full timestamp e.g. 2026-03-18T00:00 or 2026-03-18 00:00:00
        if (value.length() >= 10) {
            String firstTen = value.substring(0, 10);
            try {
                return Optional.of(LocalDate.parse(firstTen, DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (DateTimeParseException ignored) {
            }
        }

        String normalized = value
                .replace('年', '-')
                .replace('月', '-')
                .replace("日", "")
                .replace('.', '-')
                .replace('/', '-')
                .trim();

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("d-M-uuuu"),
                DateTimeFormatter.ofPattern("uuuu-M-d"),
                DateTimeFormatter.ofPattern("M-d-uuuu")
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return Optional.of(LocalDate.parse(normalized, formatter));
            } catch (DateTimeParseException ignored) {
            }
        }

        return Optional.empty();
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
     * Find the data sheet in the workbook.
     * Priority: sheet name containing "梱包指示" > "Dg" > active sheet
     */
    private Sheet findDataSheet(Workbook workbook) {
        // Priority 1: sheet containing "梱包指示"
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String name = workbook.getSheetName(i);
            if (name != null && name.contains("梱包指示")) {
                log.info("Found target sheet by '梱包指示': {}", name);
                return workbook.getSheetAt(i);
            }
        }
        // Priority 2: sheet containing "Dg"
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String name = workbook.getSheetName(i);
            if (name != null && name.contains("Dg")) {
                log.info("Found target sheet by 'Dg': {}", name);
                return workbook.getSheetAt(i);
            }
        }
        // Fallback: active sheet
        log.info("No target sheet found, using active sheet index: {}", workbook.getActiveSheetIndex());
        return workbook.getSheetAt(workbook.getActiveSheetIndex());
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
        return getAllOrders(pageable, null, null, null, null);
    }

    /**
     * Get all orders (Admin) with optional filters.
     */
    public Page<OrderResponseDTO> getAllOrders(
            Pageable pageable,
            String keyword,
            OrderStatus status,
            LocalDate fromDate,
            LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("Khoảng ngày không hợp lệ: Từ ngày phải nhỏ hơn hoặc bằng Đến ngày");
        }

        String normalizedKeyword = keyword != null ? keyword.trim().toLowerCase() : null;
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateExclusive = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;

        Specification<Order> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (normalizedKeyword != null && !normalizedKeyword.isBlank()) {
                String likePattern = "%" + normalizedKeyword + "%";
                Join<Order, User> userJoin = root.join("user", JoinType.LEFT);
                Join<Order, Company> companyJoin = root.join("company", JoinType.LEFT);

                predicates.add(cb.or(
                        cb.like(cb.lower(cb.coalesce(root.get("orderNumber"), "")), likePattern),
                        cb.like(cb.lower(cb.coalesce(userJoin.get("fullName"), "")), likePattern),
                        cb.like(cb.lower(cb.coalesce(companyJoin.get("companyName"), "")), likePattern)));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (fromDateTime != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDateTime));
            }

            if (toDateExclusive != null) {
                predicates.add(cb.lessThan(root.get("createdAt"), toDateExclusive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return orderRepository.findAll(specification, pageable).map(this::mapToDTO);
    }

    /**
     * Get user's orders (optionally filtered by status)
     */
    public Page<OrderResponseDTO> getUserOrders(Long userId, OrderStatus status, Pageable pageable) {
        if (status != null) {
            return orderRepository.findByUserIdAndStatus(userId, status, pageable).map(this::mapToDTO);
        }
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
            BigDecimal unitPrice = request.getUnitPrice();

            // Auto-fill from drawing_categories if no price provided
            if ((unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0)
                    && item.getDrawingNumber() != null && !item.getDrawingNumber().isBlank()) {
                unitPrice = quotePricingService.getDefaultPriceForDrawing(item.getDrawingNumber());
            }

            if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 khi duyệt sản phẩm");
            }
            item.setUnitPrice(unitPrice);

            // Sync price back to drawing_categories DB
            if (item.getDrawingNumber() != null && !item.getDrawingNumber().isBlank()) {
                quotePricingService.upsertPrice(item.getDrawingNumber(), null, unitPrice);
            }
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
        // Đặt cọc 60% tổng giá trị đơn hàng
        BigDecimal depositAmount = totalPrice
                .multiply(BigDecimal.valueOf(0.6))
                .setScale(0, RoundingMode.HALF_UP);
        order.setDepositAmount(depositAmount);

        // Generate SePay QR URL thật
        String qrUrl = paymentService.generateSepayQrUrl(order.getOrderNumber(), depositAmount);
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
     * Confirm payment (manual or webhook)
     */
    @Transactional
    public OrderResponseDTO confirmPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        // Cho phép chuyển sang PROCESSING từ cả 2 trạng thái:
        // - DEPOSITED: đã cọc qua SePay webhook (tự động)
        // - AWAITING_PAYMENT: xác nhận thanh toán thủ công
        if (order.getStatus() != OrderStatus.AWAITING_PAYMENT
                && order.getStatus() != OrderStatus.DEPOSITED) {
            throw new IllegalStateException(
                    "Chỉ có thể bắt đầu gia công khi đơn hàng đã cọc hoặc đang chờ thanh toán. Trạng thái hiện tại: "
                            + order.getStatus());
        }

        order.setStatus(OrderStatus.PROCESSING);
        order.setPaidAt(java.time.LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);
        log.info("Payment confirmed / Processing started for order: {}", savedOrder.getOrderNumber());

        return mapToDTO(savedOrder);
    }

    /**
     * Update order status (Admin)
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        OrderStatus currentStatus = order.getStatus();
        if (newStatus == null) {
            throw new IllegalArgumentException("New status is required");
        }

        if (newStatus != currentStatus && !isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Không thể chuyển trạng thái từ " + currentStatus + " sang " + newStatus);
        }

        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);

        log.info("Order {} status updated to {}", savedOrder.getOrderNumber(), newStatus);
        return mapToDTO(savedOrder);
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> allowedTargets = switch (from) {
            case PENDING_QUOTE -> EnumSet.of(OrderStatus.AWAITING_PAYMENT, OrderStatus.CANCELLED);
            case AWAITING_PAYMENT -> EnumSet.of(OrderStatus.DEPOSITED, OrderStatus.PROCESSING, OrderStatus.CANCELLED);
            case DEPOSITED -> EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED);
            case PROCESSING -> EnumSet.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED);
            case COMPLETED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
        return allowedTargets.contains(to);
    }

    /**
     * Map Order entity to DTO
     */
    private OrderResponseDTO mapToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setUserName(order.getUser() != null ? order.getUser().getFullName() : null);
        dto.setCompanyId(order.getCompany() != null ? order.getCompany().getId() : null);
        dto.setCompanyName(order.getCompany() != null ? order.getCompany().getCompanyName() : null);
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDepositAmount(order.getDepositAmount());
        dto.setPaymentQrUrl(order.getPaymentQrUrl());
        dto.setPaidAt(order.getPaidAt());
        dto.setNotes(order.getNotes());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        Map<String, BigDecimal> weightByDrawing = loadWeightByDrawing(order.getItems());

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
            // Delivery date
            itemDTO.setDeliveryDate(item.getDeliveryDate() != null ? item.getDeliveryDate().toString() : null);
            itemDTO.setWeight(weightByDrawing.get(item.getDrawingNumber()));
            return itemDTO;
        }).collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    private Map<String, BigDecimal> loadWeightByDrawing(List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> drawingNumbers = items.stream()
                .map(OrderItem::getDrawingNumber)
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .toList();

        if (drawingNumbers.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, BigDecimal> result = new HashMap<>();
        List<DrawingMeta> metas = drawingMetaRepository.findByDrawingNumberIn(drawingNumbers);
        for (DrawingMeta meta : metas) {
            result.put(meta.getDrawingNumber(), meta.getWeight());
        }
        return result;
    }
}
