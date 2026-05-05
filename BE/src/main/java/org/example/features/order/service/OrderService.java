package org.example.features.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.User;
import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.example.features.contract.service.ContractService;
import org.example.features.order.dto.CartOrderRequestDTO;
import org.example.features.order.dto.DelayDeliveryRequestDTO;
import org.example.features.order.dto.ItemReviewRequestDTO;
import org.example.features.order.dto.OrderHistoryEventDTO;
import org.example.features.order.dto.OrderItemDTO;
import org.example.features.order.dto.OrderRevisionSummaryDTO;
import org.example.features.order.dto.OrderResponseDTO;
import org.example.features.order.dto.QuoteRequestDTO;
import org.example.features.order.entity.OrderEventType;
import org.example.features.order.entity.OrderEvent;
import org.example.features.order.entity.ItemReviewStatus;
import org.example.features.order.entity.ImportBatchStatus;
import org.example.features.order.entity.ImportSourceType;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderRevisionSource;
import org.example.features.order.entity.OrderImportBatch;
import org.example.features.order.entity.OrderImportItem;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.entity.OrderType;
import org.example.features.order.repository.OrderEventRepository;
import org.example.features.order.repository.OrderImportBatchRepository;
import org.example.features.order.repository.OrderItemRepository;
import org.example.features.order.repository.OrderRepository;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.payment.service.PaymentMilestoneService;
import org.example.features.payment.service.PaymentService;
import org.example.features.productadmin.AdminProductService;
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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.features.complaint.entity.OrderComplaint;
import org.example.features.complaint.entity.ComplaintStatus;
import org.example.features.complaint.repository.OrderComplaintRepository;

/**
 * Order Service
 * Handles order creation, Excel import, and order lifecycle management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private static final String FIELD_VNN_NO = "vnnNo";
    private static final String FIELD_ITEM_CODE = "itemCode";
    private static final String FIELD_DRAWING_NUMBER = "drawingNumber";
    private static final String FIELD_PART_NAME = "partName";
    private static final String FIELD_SPECIFICATION = "specification";
    private static final String FIELD_MATERIAL = "material";
    private static final String FIELD_QUANTITY = "quantity";
    private static final String FIELD_DELIVERY_DATE = "deliveryDate";

    private static final List<String> REQUIRED_EXCEL_FIELDS = List.of(
        FIELD_VNN_NO,
        FIELD_ITEM_CODE,
        FIELD_DRAWING_NUMBER,
        FIELD_PART_NAME,
        FIELD_SPECIFICATION,
        FIELD_MATERIAL,
        FIELD_QUANTITY,
        FIELD_DELIVERY_DATE);

    private static final Map<String, List<String>> EXCEL_HEADER_ALIASES = buildExcelHeaderAliases();

    private static final Map<String, String> EXCEL_FIELD_DISPLAY_NAMES = Map.of(
        FIELD_VNN_NO, "VNN、NO",
        FIELD_ITEM_CODE, "Item Code",
        FIELD_DRAWING_NUMBER, "Drawing Number",
        FIELD_PART_NAME, "Part Name",
        FIELD_SPECIFICATION, "Spec.",
        FIELD_MATERIAL, "Material",
        FIELD_QUANTITY, "QTY",
        FIELD_DELIVERY_DATE, "Delivery Date");

    private final OrderRepository orderRepository;
    private final OrderComplaintRepository orderComplaintRepository;
    private final OrderEventRepository orderEventRepository;
    private final OrderImportBatchRepository orderImportBatchRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ContractService contractService;
    private final PaymentService paymentService;
    private final PaymentMilestoneService paymentMilestoneService;
    private final QuotePricingService quotePricingService;
    private final DrawingMetaRepository drawingMetaRepository;
    private final AdminProductService adminProductService;
    private final UserNotificationService userNotificationService;
    private final OrderAuditService orderAuditService;

    private static Map<String, List<String>> buildExcelHeaderAliases() {
        Map<String, List<String>> aliases = new LinkedHashMap<>();
        aliases.put(FIELD_VNN_NO, List.of("vnn_no", "vnn no", "vnn", "受注番号", "注文番号", "order no", "order number"));
        aliases.put(FIELD_ITEM_CODE, List.of("item code", "itemcode", "品目コード", "品目cd", "ma hang", "mã hàng"));
        aliases.put(FIELD_DRAWING_NUMBER,
            List.of("drawing number", "drawing no", "drawing", "図番", "ban ve", "bản vẽ"));
        aliases.put(FIELD_PART_NAME, List.of("part name", "item name", "品名", "ten chi tiet", "tên chi tiết", "ten hang", "tên hàng"));
        aliases.put(FIELD_SPECIFICATION, List.of("specification", "spec", "型式", "quy cach", "quy cách"));
        aliases.put(FIELD_MATERIAL, List.of("material", "material type", "材質", "chat lieu", "chất liệu"));
        aliases.put(FIELD_QUANTITY, List.of("quantity", "qty", "数量", "so luong", "số lượng"));
        aliases.put(FIELD_DELIVERY_DATE,
            List.of("希望納期", "出荷日", "納期", "delivery date", "delivery", "due date", "ngay xuat", "ngày xuất"));
        return Collections.unmodifiableMap(aliases);
    }

    private String normalizeImportCode(String rawCode) {
        if (rawCode == null) {
            return null;
        }
        return rawCode.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeCancelReason(String reason, boolean required) {
        if (reason == null || reason.isBlank()) {
            if (required) {
                throw new IllegalArgumentException("Vui lòng nhập lý do hủy đơn");
            }
            return null;
        }

        String trimmed = reason.trim();
        if (trimmed.length() > 500) {
            throw new IllegalArgumentException("Lý do hủy không được vượt quá 500 ký tự");
        }
        return trimmed;
    }

    private String normalizeItemNotes(String notes) {
        if (notes == null) {
            return null;
        }

        String trimmed = notes.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (trimmed.length() > 2000) {
            throw new IllegalArgumentException("Ghi chú sản phẩm không được vượt quá 2000 ký tự");
        }

        return trimmed;
    }

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

            OrderImportBatch batch = createOrUpdateImportBatchFromParsedItems(
                    items,
                    user,
                    company,
                    ImportSourceType.CUSTOMER,
                    file.getOriginalFilename());
            log.info("Order import staging completed successfully: {}", batch.getImportCode());

            return mapImportBatchToDTO(batch);

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

            OrderImportBatch batch = createOrUpdateImportBatchFromParsedItems(
                    items,
                    companyUser,
                    company,
                    ImportSourceType.ADMIN,
                    file.getOriginalFilename());
            log.info("Admin imported order to staging successfully: {}", batch.getImportCode());
            return mapImportBatchToDTO(batch);
        } catch (IOException e) {
            log.error("Error reading Excel file", e);
            throw new RuntimeException("Failed to read Excel file: " + e.getMessage());
        }
    }

    public Page<OrderResponseDTO> getPendingImportBatches(Pageable pageable, String keyword) {
        return orderImportBatchRepository
                .searchByStatus(ImportBatchStatus.PENDING_APPROVAL, keyword, pageable)
                .map(this::mapImportBatchToDTO);
    }

    public List<OrderResponseDTO> getMyPendingImportBatches(Long userId) {
        return getMyImportBatches(userId, ImportBatchStatus.PENDING_APPROVAL);
    }

    public List<OrderResponseDTO> getMyImportBatches(Long userId, ImportBatchStatus status) {
        ImportBatchStatus targetStatus = status != null ? status : ImportBatchStatus.PENDING_APPROVAL;
        return orderImportBatchRepository
                .findByUserIdAndStatusOrderByCreatedAtDesc(userId, targetStatus)
                .stream()
                .map(this::mapImportBatchToDTO)
                .toList();
    }

    public OrderResponseDTO getImportBatchById(Long batchId, Long requestingUserId, boolean isAdmin) {
        OrderImportBatch batch = orderImportBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found"));

        if (!isAdmin && (batch.getUser() == null || !batch.getUser().getId().equals(requestingUserId))) {
            throw new SecurityException("Unauthorized access to import batch");
        }

        return mapImportBatchToDTO(batch);
    }

    @Transactional
    public OrderResponseDTO approveImportBatch(Long batchId) {
        OrderImportBatch batch = orderImportBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found"));

        if (batch.getStatus() != ImportBatchStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Chỉ có thể duyệt batch đang ở trạng thái CHỜ DUYỆT");
        }

        String normalizedOrderCode = normalizeImportCode(batch.getImportCode());
        OrderStatus previousStatus = orderRepository
            .findByOrderNumberIgnoreCase(normalizedOrderCode)
            .map(Order::getStatus)
            .orElse(null);

        Order order = createOrUpdateOrderFromApprovedBatch(batch);
        batch.setStatus(ImportBatchStatus.APPROVED);
        batch.setApprovedOrder(order);
        orderImportBatchRepository.save(batch);

        OrderResponseDTO dto = mapToDTO(order);
        OrderRevisionSource revisionSource = batch.getSourceType() == ImportSourceType.ADMIN
            ? OrderRevisionSource.ADMIN_IMPORT
            : OrderRevisionSource.CUSTOMER_IMPORT;
        orderAuditService.recordImportApprovedRevision(
            order,
            batch,
            revisionSource,
            dto,
            previousStatus,
            order.getStatus(),
            null,
            "ADMIN",
            "Duyệt import batch " + batch.getImportCode());

        userNotificationService.pushOrderNotification(
            order,
            NotificationType.ORDER,
            "Đơn hàng đã được duyệt",
            "Đơn " + order.getOrderNumber() + " đã được admin duyệt, đang chờ báo giá.",
            "order-status-" + order.getId() + "-PENDING_QUOTE");

        log.info("Approved import batch {} -> order {}", batch.getImportCode(), order.getOrderNumber());
        return dto;
    }

    @Transactional
    public void cancelImportBatch(Long batchId, Long requestingUserId, boolean isAdmin, String cancelReason) {
        OrderImportBatch batch = orderImportBatchRepository.findById(batchId)
                .orElseThrow(() -> new IllegalArgumentException("Import batch not found"));

        String normalizedCancelReason = normalizeCancelReason(cancelReason, true);

        if (!isAdmin && (batch.getUser() == null || !batch.getUser().getId().equals(requestingUserId))) {
            throw new SecurityException("Unauthorized access to import batch");
        }

        if (batch.getStatus() != ImportBatchStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("Chỉ có thể hủy import khi đang ở trạng thái CHỜ DUYỆT");
        }

        batch.setStatus(ImportBatchStatus.REJECTED);
        batch.setRejectionReason(normalizedCancelReason);
        batch.setRejectedByRole(isAdmin ? "ADMIN" : "CUSTOMER");
        orderImportBatchRepository.save(batch);

        String normalizedCode = normalizeImportCode(batch.getImportCode());
        orderRepository.findByOrderNumberIgnoreCase(normalizedCode)
            .filter(order -> order.getCompany() != null
                && batch.getCompany() != null
                && order.getCompany().getId().equals(batch.getCompany().getId()))
            .ifPresent(order -> orderAuditService.recordStatusEvent(
                order,
                OrderEventType.IMPORT_REJECTED,
                null,
                null,
                requestingUserId,
                isAdmin ? "ADMIN" : "CUSTOMER",
                normalizedCancelReason));

            if (isAdmin && batch.getUser() != null) {
                userNotificationService.pushNotificationToUser(
                    batch.getUser().getId(),
                    null,
                    NotificationType.ORDER,
                    "Đơn import bị từ chối",
                    "Đơn " + batch.getImportCode() + " đã bị admin từ chối. Lý do: " + normalizedCancelReason,
                    "import-rejected-" + batch.getId());
            }

        log.info("Import batch {} cancelled by user {}", batch.getImportCode(), requestingUserId);
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
        order.setOrderType(OrderType.READY_MADE);

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

        for (CartOrderRequestDTO.CartItemDTO cartItem : request.getItems()) {
            if (cartItem.getName() != null && cartItem.getQuantity() != null && cartItem.getQuantity() > 0) {
                // Gọi thẳng, nếu hết hàng BE sẽ tự ném lỗi có chữ tiếng Việt!
                adminProductService.deductStock(cartItem.getName(), cartItem.getQuantity());
            }
        }

        return mapToDTO(saved);
    }

    /**
     * Admin: set delivery date and mark READY_MADE order ready to ship.
     * PROCESSING -> AWAITING_DELIVERY
     */
    @Transactional
    public OrderResponseDTO setDeliveryDateAndReadyToDeliver(Long orderId, LocalDate deliveryDate) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));

        if (order.getOrderType() != OrderType.READY_MADE) {
            throw new IllegalStateException("Chức năng này chỉ áp dụng cho đơn sản phẩm phôi (READY_MADE)");
        }

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Đơn hàng phải đang ở trạng thái 'Đang chuẩn bị' (PROCESSING)");
        }

        if (deliveryDate == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày giao hàng");
        }

        if (deliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày giao hàng không được ở trong quá khứ");
        }

        order.setDeliveryDate(deliveryDate);
        order.setStatus(OrderStatus.AWAITING_DELIVERY);
        Order saved = orderRepository.save(order);

        notifyOrderStatusTransition(saved, OrderStatus.PROCESSING, OrderStatus.AWAITING_DELIVERY);
        orderAuditService.recordStatusEvent(
            saved,
            OrderEventType.STATUS_CHANGED,
            OrderStatus.PROCESSING,
            OrderStatus.AWAITING_DELIVERY,
            null,
            "ADMIN",
            "Admin đã đặt ngày giao và đánh dấu sẵn sàng giao hàng");

        log.info("READY_MADE order {} -> AWAITING_DELIVERY, delivery={}", saved.getOrderNumber(), deliveryDate);
        return mapToDTO(saved);
    }

    private OrderImportBatch createOrUpdateImportBatchFromParsedItems(
            List<OrderItemDTO> items,
            User owner,
            Company company,
            ImportSourceType sourceType,
            String originalFilename) {
        String importCode = items.stream()
                .map(OrderItemDTO::getUnit)
                .filter(u -> u != null && !u.isBlank())
                .findFirst()
                .orElseGet(this::generateOrderNumber);

        importCode = normalizeImportCode(importCode);
        if (importCode == null || importCode.isBlank()) {
            importCode = generateOrderNumber();
        }

        Optional<Order> existingOrderOpt = orderRepository.findByOrderNumberIgnoreCase(importCode);
        if (existingOrderOpt.isPresent()) {
            Order existingOrder = existingOrderOpt.get();
            Long existingCompanyId = existingOrder.getCompany() != null ? existingOrder.getCompany().getId() : null;

            if (existingCompanyId == null || !existingCompanyId.equals(company.getId())) {
                throw new IllegalArgumentException("Mã đơn hàng đã tồn tại ở công ty khác: " + importCode);
            }

                if (!isAllowedStatusForReimport(existingOrder.getStatus())) {
                throw new IllegalArgumentException(
                    "Mã đơn hàng (VNN NO) đã tồn tại và đang ở trạng thái "
                        + existingOrder.getStatus()
                        + ". Chỉ cho phép import lại khi đơn đang CHỜ BÁO GIÁ, CHỜ DUYỆT hoặc ĐÃ HỦY.");
            }
        }

        OrderImportBatch batch = orderImportBatchRepository
        .findByImportCodeIgnoreCaseAndCompanyIdAndStatus(importCode, company.getId(), ImportBatchStatus.PENDING_APPROVAL)
                .orElseGet(OrderImportBatch::new);

        if (batch.getId() != null) {
            batch.getItems().clear();
            log.info("Updated existing pending import batch by VNN_NO: {}", importCode);
        }

        batch.setImportCode(importCode);
        batch.setUser(owner);
        batch.setCompany(company);
        batch.setStatus(ImportBatchStatus.PENDING_APPROVAL);
        batch.setSourceType(sourceType);
        batch.setOriginalFilename(originalFilename);
        batch.setApprovedOrder(null);

        for (OrderItemDTO itemDTO : items) {
            OrderImportItem item = new OrderImportItem();
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

            batch.addItem(item);
        }

        return orderImportBatchRepository.save(batch);
    }

    private Order createOrUpdateOrderFromApprovedBatch(OrderImportBatch batch) {
        String orderNumber = normalizeImportCode(batch.getImportCode());

        Order order;
        Optional<Order> existing = orderRepository.findByOrderNumberIgnoreCase(orderNumber);
        if (existing.isPresent()) {
            Order existingOrder = existing.get();
            Long existingCompanyId = existingOrder.getCompany() != null ? existingOrder.getCompany().getId() : null;
            if (existingCompanyId == null || !existingCompanyId.equals(batch.getCompany().getId())) {
                throw new IllegalStateException("Mã đơn hàng đã tồn tại ở công ty khác: " + orderNumber);
            }

            if (!isAllowedStatusForReimport(existingOrder.getStatus())) {
                throw new IllegalStateException(
                        "Không thể duyệt import cho mã đơn " + orderNumber
                                + " vì đơn hiện tại đang ở trạng thái " + existingOrder.getStatus()
                                + ". Chỉ cho phép khi đang CHỜ BÁO GIÁ, CHỜ DUYỆT hoặc ĐÃ HỦY.");
            }

            existingOrder.getItems().clear();
            order = existingOrder;
            log.info("Replace items for existing order {} from approved import batch", orderNumber);
        } else {
            order = new Order();
            order.setOrderNumber(orderNumber);
        }

        order.setUser(batch.getUser());
        order.setCompany(batch.getCompany());
        order.setStatus(OrderStatus.PENDING_QUOTE);
        order.setOrderType(OrderType.CUSTOM_MANUFACTURING);
        order.setTotalPrice(null);
        order.setDepositAmount(null);
        order.setPaymentQrUrl(null);
        order.setPaymentDeadline(null);
        order.setPaidAt(null);
        order.setNotes(null);

        for (OrderImportItem importedItem : batch.getItems()) {
            OrderItem item = new OrderItem();
            item.setItemCode(importedItem.getItemCode());
            item.setDrawingNumber(importedItem.getDrawingNumber());
            item.setItemName(importedItem.getItemName());
            item.setSpecification(importedItem.getSpecification());
            item.setMaterialType(importedItem.getMaterialType());
            item.setQuantity(importedItem.getQuantity());
            item.setUnit(importedItem.getUnit());
            item.setNotes(importedItem.getNotes());
            item.setDeliveryDate(importedItem.getDeliveryDate());
            item.setReviewStatus(ItemReviewStatus.PENDING_REVIEW);
            order.addItem(item);
        }

        return orderRepository.save(order);
    }

    /**
     * Parse Excel file to extract order items
     * Required business columns are detected by header name (STT optional).
     */
    private List<OrderItemDTO> parseExcelFile(MultipartFile file) throws IOException {
        List<OrderItemDTO> items = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = findDataSheet(workbook);
            Map<String, Integer> columnMap = resolveRequiredColumnMap(sheet);
            log.info("Resolved Excel header mapping: {}", columnMap);

            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                if (!hasBusinessData(row, columnMap)) {
                    continue;
                }

                OrderItemDTO item = new OrderItemDTO();

                String vnnNo = getCellValueByField(row, columnMap, FIELD_VNN_NO);
                String itemCode = getCellValueByField(row, columnMap, FIELD_ITEM_CODE);
                String drawingNumber = getCellValueByField(row, columnMap, FIELD_DRAWING_NUMBER);
                String partName = getCellValueByField(row, columnMap, FIELD_PART_NAME);
                String specification = getCellValueByField(row, columnMap, FIELD_SPECIFICATION);
                String material = getCellValueByField(row, columnMap, FIELD_MATERIAL);
                String quantityStr = getCellValueByField(row, columnMap, FIELD_QUANTITY);
                String deliveryDate = getCellValueByField(row, columnMap, FIELD_DELIVERY_DATE);

                List<String> missingFields = new ArrayList<>();
                if (vnnNo.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_VNN_NO));
                if (itemCode.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_ITEM_CODE));
                if (drawingNumber.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_DRAWING_NUMBER));
                if (partName.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_PART_NAME));
                if (specification.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_SPECIFICATION));
                if (material.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_MATERIAL));
                if (quantityStr.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_QUANTITY));
                if (deliveryDate.isBlank()) missingFields.add(EXCEL_FIELD_DISPLAY_NAMES.get(FIELD_DELIVERY_DATE));

                if (!missingFields.isEmpty()) {
                    log.warn("Skipping row {} - missing required values: {}", i + 1, String.join(", ", missingFields));
                    continue;
                }

                // Store VNN_NO in unit field for existing order grouping behavior.
                item.setUnit(vnnNo);
                item.setItemCode(itemCode);
                item.setDrawingNumber(drawingNumber);
                item.setItemName(partName);
                item.setSpecification(specification);
                item.setMaterial(material);

                String quantityDigits = quantityStr.replaceAll("[^0-9]", "");
                if (quantityDigits.isEmpty()) {
                    log.warn("Invalid quantity at row {}: {}", i + 1, quantityStr);
                    continue;
                }

                try {
                    item.setQuantity(Integer.parseInt(quantityDigits));
                } catch (NumberFormatException e) {
                    log.warn("Invalid quantity at row {}: {}", i + 1, quantityStr);
                    continue;
                }

                Optional<LocalDate> parsedDeliveryDate = parseFlexibleDate(deliveryDate);
                if (parsedDeliveryDate.isEmpty()) {
                    log.warn("Invalid delivery date at row {}: {}", i + 1, deliveryDate);
                    continue;
                }
                item.setDeliveryDate(parsedDeliveryDate.get().toString());

                if (item.getQuantity() <= 0) {
                    log.warn("Skipping row {} - quantity must be greater than 0", i + 1);
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

    private Map<String, Integer> resolveRequiredColumnMap(Sheet sheet) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Excel file is missing header row");
        }

        Map<String, Integer> resolvedMap = new LinkedHashMap<>();
        int lastCellNum = Math.max(headerRow.getLastCellNum(), (short) 0);

        for (int col = 0; col < lastCellNum; col++) {
            String normalizedHeader = normalizeHeader(getCellValueAsString(headerRow.getCell(col)));
            if (normalizedHeader.isEmpty()) {
                continue;
            }

            for (Map.Entry<String, List<String>> entry : EXCEL_HEADER_ALIASES.entrySet()) {
                if (resolvedMap.containsKey(entry.getKey())) {
                    continue;
                }
                if (matchesAnyAlias(normalizedHeader, entry.getValue())) {
                    resolvedMap.put(entry.getKey(), col);
                    break;
                }
            }
        }

        List<String> missing = REQUIRED_EXCEL_FIELDS.stream()
                .filter(field -> !resolvedMap.containsKey(field))
                .map(field -> EXCEL_FIELD_DISPLAY_NAMES.get(field) + " (aliases: " + String.join(", ", EXCEL_HEADER_ALIASES.get(field)) + ")")
                .toList();

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required headers: " + String.join("; ", missing));
        }

        return resolvedMap;
    }

    private String getCellValueByField(Row row, Map<String, Integer> columnMap, String field) {
        Integer index = columnMap.get(field);
        if (index == null) {
            return "";
        }
        return getCellValueAsString(row.getCell(index));
    }

    private boolean hasBusinessData(Row row, Map<String, Integer> columnMap) {
        for (String field : REQUIRED_EXCEL_FIELDS) {
            if (!getCellValueByField(row, columnMap, field).isBlank()) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAnyAlias(String normalizedHeader, List<String> aliases) {
        Set<String> normalizedAliases = aliases.stream()
                .map(this::normalizeHeader)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        for (String alias : normalizedAliases) {
            if (alias.isBlank()) {
                continue;
            }
            if (normalizedHeader.equals(alias)) {
                return true;
            }
            // Accept composite headers such as "item code (品目コード)".
            if (alias.length() >= 4 && normalizedHeader.contains(alias)) {
                return true;
            }
        }

        return false;
    }

    private String normalizeHeader(String rawHeader) {
        if (rawHeader == null) {
            return "";
        }
        return rawHeader
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace('＿', '_')
                .replaceAll("[^\\p{L}\\p{N}]+", "");
    }

    private Optional<LocalDate> parseFlexibleDate(String rawValue) {
        if (rawValue == null) {
            return Optional.empty();
        }

        String value = rawValue.trim();
        if (value.isEmpty()) {
            return Optional.empty();
        }

        int tIndex = value.indexOf('T');
        if (tIndex > 0) {
            value = value.substring(0, tIndex).trim();
        }

        int spaceIndex = value.indexOf(' ');
        if (spaceIndex > 0) {
            value = value.substring(0, spaceIndex).trim();
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

        String digitsOnly = normalized.replaceAll("[^0-9]", "");
        if (digitsOnly.length() == 8) {
            DateTimeFormatter[] compactFormatters = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("uuuuMMdd"),
                DateTimeFormatter.ofPattern("ddMMyyyy"),
                DateTimeFormatter.ofPattern("MMddyyyy")
            };

            for (DateTimeFormatter formatter : compactFormatters) {
            try {
                return Optional.of(LocalDate.parse(digitsOnly, formatter));
            } catch (DateTimeParseException ignored) {
            }
            }
        }

        DateTimeFormatter[] formatters = new DateTimeFormatter[] {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("d-M-uuuu"),
                DateTimeFormatter.ofPattern("uuuu-M-d"),
            DateTimeFormatter.ofPattern("M-d-uuuu"),
            DateTimeFormatter.ofPattern("d-M-uu"),
            DateTimeFormatter.ofPattern("M-d-uu")
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
        return getAllOrders(pageable, null, null, null, null, null);
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
        return getAllOrders(pageable, keyword, status, fromDate, toDate, null);
    }

    /**
     * Get all orders (Admin) with optional filters including orderType.
     */
    public Page<OrderResponseDTO> getAllOrders(
            Pageable pageable,
            String keyword,
            OrderStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            OrderType orderType) {
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

            if (orderType != null) {
                predicates.add(cb.equal(root.get("orderType"), orderType));
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
     * Get user's orders (optionally filtered by status and/or orderType)
     */
    public Page<OrderResponseDTO> getUserOrders(Long userId, OrderStatus status, OrderType orderType, Pageable pageable) {
        if (status != null && orderType != null) {
            return orderRepository.findByUserIdAndStatusAndOrderType(userId, status, orderType, pageable).map(this::mapToDTO);
        } else if (orderType != null) {
            return orderRepository.findByUserIdAndOrderType(userId, orderType, pageable).map(this::mapToDTO);
        } else if (status != null) {
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

    public List<OrderHistoryEventDTO> getOrderHistory(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to order");
        }

        return orderAuditService.getOrderHistory(orderId);
    }

    public List<OrderRevisionSummaryDTO> getOrderRevisions(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to order");
        }

        return orderAuditService.getOrderRevisions(orderId);
    }

    public OrderResponseDTO getOrderRevisionSnapshot(Long orderId, Integer revisionNo, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to order");
        }

        return orderAuditService.getOrderRevisionSnapshot(orderId, revisionNo);
    }

    /**
     * User: Cancel order before deposit is paid
     */
    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId, Long requestingUserId, String cancelReason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        String normalizedCancelReason = normalizeCancelReason(cancelReason, true);

        if (!order.getUser().getId().equals(requestingUserId)) {
            throw new SecurityException("Unauthorized access to order");
        }

        OrderStatus currentStatus = order.getStatus();
        if (currentStatus != OrderStatus.PENDING_APPROVAL
                && currentStatus != OrderStatus.PENDING_QUOTE
            && currentStatus != OrderStatus.AWAITING_CONTRACT
                && currentStatus != OrderStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException(
                "Chỉ có thể hủy đơn trước khi cọc (Chờ duyệt, Chờ báo giá, Chờ xác nhận hợp đồng hoặc Chờ thanh toán). Trạng thái hiện tại: "
                            + currentStatus);
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setPaymentDeadline(null);
        order.setPaymentQrUrl(null);

        // TRẢ LẠI TỒN KHO KHI KHÁCH TỰ HỦY
        for (org.example.features.order.entity.OrderItem item : order.getItems()) {
            if (item.getItemName() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                try {
                    // Trả lại kho khi khách chủ động hủy đơn
                    adminProductService.restoreStock(item.getItemName(), item.getQuantity());
                } catch (Exception ex) {
                    log.warn("Lỗi khi trả lại tồn kho cho món '{}': {}", item.getItemName(), ex.getMessage());
                }
            }
        }

        Order saved = orderRepository.save(order);

        notifyOrderStatusTransition(saved, currentStatus, OrderStatus.CANCELLED);
        orderAuditService.recordStatusEvent(
            saved,
            OrderEventType.ORDER_CANCELLED,
            currentStatus,
            OrderStatus.CANCELLED,
            requestingUserId,
            "CUSTOMER",
            normalizedCancelReason);

        log.info("Order {} cancelled by user {}", saved.getOrderNumber(), requestingUserId);
        return mapToDTO(saved);
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
     * Customer/Admin updates free-form note on an individual order item.
     */
    @Transactional
    public OrderResponseDTO updateOrderItemNotes(Long orderId,
                                                 Long itemId,
                                                 String notes,
                                                 Long requestingUserId,
                                                 String requestingRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        boolean isAdmin = "ADMIN".equalsIgnoreCase(requestingRole);
        if (!isAdmin) {
            Long ownerId = order.getUser() != null ? order.getUser().getId() : null;
            if (requestingUserId == null || ownerId == null || !ownerId.equals(requestingUserId)) {
                throw new SecurityException("Unauthorized access to order");
            }
        }

        OrderItem item = order.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found in this order"));

        item.setNotes(normalizeItemNotes(notes));
        orderItemRepository.save(item);
        log.info("Item {} notes in order {} updated by {}", itemId, order.getOrderNumber(), requestingRole);

        return mapToDTO(order);
    }

    /**
     * Admin sets quote price and moves order to contract confirmation step
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

        // Persist latest draft values from admin UI before locking quote.
        if (quoteRequest.getItems() != null && !quoteRequest.getItems().isEmpty()) {
            Map<Long, QuoteRequestDTO.QuoteItemUpdateDTO> updatesById = quoteRequest.getItems().stream()
                    .filter(update -> update != null && update.getId() != null)
                    .collect(Collectors.toMap(
                            QuoteRequestDTO.QuoteItemUpdateDTO::getId,
                            update -> update,
                            (left, right) -> right));

            for (OrderItem item : order.getItems()) {
                QuoteRequestDTO.QuoteItemUpdateDTO update = updatesById.get(item.getId());
                if (update == null) {
                    continue;
                }

                if (update.getUnitPrice() != null) {
                    if (item.getReviewStatus() == ItemReviewStatus.APPROVED) {
                        if (update.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0 cho sản phẩm đã duyệt");
                        }
                        item.setUnitPrice(update.getUnitPrice());

                        if (item.getDrawingNumber() != null && !item.getDrawingNumber().isBlank()) {
                            quotePricingService.upsertPrice(item.getDrawingNumber(), null, update.getUnitPrice());
                        }
                    } else if (update.getUnitPrice().compareTo(BigDecimal.ZERO) > 0) {
                        item.setUnitPrice(update.getUnitPrice());
                    } else {
                        item.setUnitPrice(null);
                    }
                }

                item.setAdminNote(normalizeItemNotes(update.getAdminNote()));
            }
        }

        // Auto-calculate total from approved items: sum(unitPrice * quantity)
        BigDecimal totalPrice = order.getItems().stream()
                .filter(item -> item.getReviewStatus() == ItemReviewStatus.APPROVED && item.getUnitPrice() != null)
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

        // Contract-first flow for custom manufacturing
        order.setPaymentQrUrl(null);
        order.setPaymentDeadline(null);
        order.setStatus(OrderStatus.AWAITING_CONTRACT);

        if (quoteRequest.getNotes() != null) {
            order.setNotes(quoteRequest.getNotes());
        }

        Order savedOrder = orderRepository.save(order);
        contractService.createContractForQuotedOrder(savedOrder);
        paymentMilestoneService.initializeMilestones(savedOrder);

        notifyOrderStatusTransition(savedOrder, OrderStatus.PENDING_QUOTE, OrderStatus.AWAITING_CONTRACT);
        orderAuditService.recordStatusEvent(
            savedOrder,
            OrderEventType.QUOTE_SET,
            OrderStatus.PENDING_QUOTE,
            OrderStatus.AWAITING_CONTRACT,
            null,
            "ADMIN",
            "Đã gửi báo giá cho đơn hàng");

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

        boolean usesMilestones = !paymentMilestoneService.getMilestones(orderId).isEmpty();

        if (usesMilestones) {
            if (order.getStatus() != OrderStatus.DEPOSITED) {
            throw new IllegalStateException(
                "Đơn hàng milestone chỉ được bắt đầu gia công sau khi admin xác nhận thanh toán cọc (DEPOSITED). Trạng thái hiện tại: "
                    + order.getStatus());
            }
        } else {
            // Cho phép chuyển sang PROCESSING từ cả 2 trạng thái:
            // - DEPOSITED: đã cọc qua SePay webhook (tự động)
            // - AWAITING_PAYMENT: xác nhận thanh toán thủ công
            if (order.getStatus() != OrderStatus.AWAITING_PAYMENT
                && order.getStatus() != OrderStatus.DEPOSITED) {
            throw new IllegalStateException(
                "Chỉ có thể bắt đầu gia công khi đơn hàng đã cọc hoặc đang chờ thanh toán. Trạng thái hiện tại: "
                    + order.getStatus());
            }
        }

        OrderStatus previousStatus = order.getStatus();
        order.setStatus(OrderStatus.PROCESSING);
        order.setPaidAt(java.time.LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        notifyOrderStatusTransition(savedOrder, previousStatus, OrderStatus.PROCESSING);
        orderAuditService.recordStatusEvent(
            savedOrder,
            OrderEventType.PAYMENT_CONFIRMED,
            previousStatus,
            OrderStatus.PROCESSING,
            null,
            "ADMIN",
            "Đã xác nhận thanh toán và bắt đầu xử lý");

        log.info("Payment confirmed / Processing started for order: {}", savedOrder.getOrderNumber());

        /*
        // Trừ tồn kho cho từng sản phẩm trong đơn hàng
        for (org.example.features.order.entity.OrderItem item : savedOrder.getItems()) {
            if (item.getItemName() != null && item.getQuantity() != null && item.getQuantity() > 0) {
                try {
                    adminProductService.deductStock(item.getItemName(), item.getQuantity());
                } catch (Exception ex) {
                    log.warn("deductStock failed for item='{}': {}", item.getItemName(), ex.getMessage());
                }
            }
        }
        */

        return mapToDTO(savedOrder);
    }

    /**
     * Admin: Mark manufacturing as finished → AWAITING_REMAINING_PAYMENT
      * Activates milestone #2 (remaining payment)
     */
    @Transactional
    public OrderResponseDTO finishProcessing(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() != OrderStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Chỉ có thể hoàn thành gia công khi đơn đang ở trạng thái PROCESSING. Hiện tại: "
                            + order.getStatus());
        }

        if (order.getTotalPrice() == null || order.getDepositAmount() == null) {
            throw new IllegalStateException("Đơn hàng chưa có giá trị hoặc tiền cọc");
        }

        java.math.BigDecimal remaining = order.getTotalPrice().subtract(order.getDepositAmount());
        if (remaining.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Khách đã thanh toán đủ, không cần chờ thanh toán đợt 2");
        }

        order.setStatus(OrderStatus.AWAITING_REMAINING_PAYMENT);

        Order saved = orderRepository.save(order);
        paymentMilestoneService.activateSecondMilestone(saved.getId());

        notifyOrderStatusTransition(saved, OrderStatus.PROCESSING, OrderStatus.AWAITING_REMAINING_PAYMENT);
        orderAuditService.recordStatusEvent(
            saved,
            OrderEventType.STATUS_CHANGED,
            OrderStatus.PROCESSING,
            OrderStatus.AWAITING_REMAINING_PAYMENT,
            null,
            "ADMIN",
            "Hoàn thành gia công, chờ thanh toán đợt 2");

        log.info("Order {} → AWAITING_REMAINING_PAYMENT, remaining={}", saved.getOrderNumber(), remaining);
        return mapToDTO(saved);
    }

    /**
     * Update order status (Admin)
     */
    @Transactional
    public OrderResponseDTO updateOrderStatus(
            Long orderId,
            OrderStatus newStatus,
            String cancelReason,
            Long actorUserId,
            String actorRole) {
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

        boolean usesMilestones = !paymentMilestoneService.getMilestones(orderId).isEmpty();
        if (usesMilestones && newStatus == OrderStatus.PROCESSING && currentStatus != OrderStatus.DEPOSITED) {
            throw new IllegalStateException("Đơn milestone chỉ được chuyển PROCESSING sau khi xác nhận thanh toán mốc 1.");
        }
        if (usesMilestones && newStatus == OrderStatus.AWAITING_DELIVERY
                && currentStatus == OrderStatus.AWAITING_REMAINING_PAYMENT) {
            throw new IllegalStateException("Vui lòng xác nhận thanh toán mốc 2 qua API verify milestone trước khi chờ giao hàng.");
        }

        String normalizedActorRole =
                actorRole == null || actorRole.isBlank()
                        ? "ADMIN"
                        : actorRole.trim().toUpperCase(Locale.ROOT);
        String normalizedCancelReason = null;

        if (newStatus == OrderStatus.CANCELLED) {
            normalizedCancelReason = normalizeCancelReason(cancelReason, true);
        }

        order.setStatus(newStatus);
        if (newStatus == OrderStatus.SHIPPING) {
            order.setShippedAt(LocalDateTime.now());
            order.setCompletedAt(null);
        } else if (newStatus == OrderStatus.COMPLETED) {
            if (order.getShippedAt() == null) {
                order.setShippedAt(LocalDateTime.now());
            }
            order.setCompletedAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.CANCELLED) {
            order.setPaymentDeadline(null);
            order.setPaymentQrUrl(null);
        }
        Order savedOrder = orderRepository.save(order);

        if (newStatus == OrderStatus.CANCELLED) {
            userNotificationService.pushOrderNotification(
                savedOrder,
                NotificationType.ORDER,
                "Đơn hàng bị từ chối",
                "Đơn " + savedOrder.getOrderNumber() + " đã bị admin từ chối. Lý do: " + normalizedCancelReason,
                "order-status-" + savedOrder.getId() + "-CANCELLED");

            orderAuditService.recordStatusEvent(
                savedOrder,
                OrderEventType.ORDER_CANCELLED,
                currentStatus,
                OrderStatus.CANCELLED,
                actorUserId,
                normalizedActorRole,
                normalizedCancelReason);
        } else {
            notifyOrderStatusTransition(savedOrder, currentStatus, newStatus);
            orderAuditService.recordStatusEvent(
                savedOrder,
                OrderEventType.STATUS_CHANGED,
                currentStatus,
                newStatus,
                actorUserId,
                normalizedActorRole,
                "Admin cập nhật trạng thái đơn hàng");
        }

        log.info("Order {} status updated to {}", savedOrder.getOrderNumber(), newStatus);
        return mapToDTO(savedOrder);
    }

    private void notifyOrderStatusTransition(Order order, OrderStatus from, OrderStatus to) {
        if (order == null || from == null || to == null || from == to) {
            return;
        }

        String title;
        String body;
        switch (to) {
            case PENDING_QUOTE -> {
                title = "Đơn hàng đã được duyệt";
                body = "Đơn " + order.getOrderNumber() + " đã được duyệt và đang chờ báo giá.";
            }
            case AWAITING_CONTRACT -> {
                title = "Vui lòng xác nhận hợp đồng";
                body = "Đơn " + order.getOrderNumber() + " đã có báo giá. Vui lòng xem và xác nhận hợp đồng trước khi thanh toán cọc.";
            }
            case AWAITING_PAYMENT -> {
                title = "Đã có báo giá cho đơn hàng";
                body = "Đơn " + order.getOrderNumber() + " đã có báo giá. Vui lòng thanh toán tiền cọc để bắt đầu xử lý.";
            }
            case DEPOSITED -> {
                title = "Đã nhận tiền cọc";
                body = "Cảm ơn bạn đã cọc trước cho đơn " + order.getOrderNumber() + ". Đơn hàng sẽ sớm được đưa vào xử lý.";
            }
            case PROCESSING -> {
                title = "Đơn hàng đang được xử lý";
                body = "Đơn " + order.getOrderNumber() + " đang trong quá trình gia công/xử lý.";
            }
            case AWAITING_REMAINING_PAYMENT -> {
                title = "Vui lòng thanh toán đợt 2";
                body = "Đơn " + order.getOrderNumber() + " đã hoàn tất gia công. Vui lòng thanh toán phần còn lại để giao hàng.";
            }
            case AWAITING_DELIVERY -> {
                title = "Đơn hàng chờ giao";
                body = "Đơn " + order.getOrderNumber() + " đã thanh toán đầy đủ và đang chờ giao hàng.";
            }
            case SHIPPING -> {
                title = "Đơn hàng đang giao";
                body = "Đơn " + order.getOrderNumber() + " đã được bàn giao cho đơn vị vận chuyển.";
            }
            case COMPLETED -> {
                title = "Đơn hàng đã hoàn thành";
                body = "Đơn " + order.getOrderNumber() + " đã được xác nhận hoàn thành. Cảm ơn bạn đã tin tưởng.";
            }
            case CANCELLED -> {
                title = "Đơn hàng đã hủy";
                body = "Đơn " + order.getOrderNumber() + " đã được hủy.";
            }
            default -> {
                return;
            }
        }

        String notificationKey = "order-status-" + order.getId() + "-" + to.name();
        userNotificationService.pushOrderNotification(order, NotificationType.ORDER, title, body, notificationKey);
    }

    private boolean isValidTransition(OrderStatus from, OrderStatus to) {
        Set<OrderStatus> allowedTargets = switch (from) {
            case PENDING_APPROVAL -> EnumSet.of(OrderStatus.PENDING_QUOTE, OrderStatus.CANCELLED);
            case PENDING_QUOTE -> EnumSet.of(OrderStatus.AWAITING_CONTRACT, OrderStatus.CANCELLED);
            case AWAITING_CONTRACT -> EnumSet.of(OrderStatus.AWAITING_PAYMENT, OrderStatus.PENDING_QUOTE, OrderStatus.CANCELLED);
            case AWAITING_PAYMENT -> EnumSet.of(OrderStatus.DEPOSITED, OrderStatus.PROCESSING,
                    OrderStatus.AWAITING_DELIVERY, OrderStatus.CANCELLED);
            case DEPOSITED -> EnumSet.of(OrderStatus.PROCESSING, OrderStatus.AWAITING_DELIVERY,
                    OrderStatus.CANCELLED);
            case PROCESSING -> EnumSet.of(OrderStatus.AWAITING_REMAINING_PAYMENT,
                    OrderStatus.AWAITING_DELIVERY, OrderStatus.COMPLETED, OrderStatus.CANCELLED);
            case AWAITING_REMAINING_PAYMENT -> EnumSet.of(OrderStatus.AWAITING_DELIVERY, OrderStatus.CANCELLED);
            case AWAITING_DELIVERY -> EnumSet.of(OrderStatus.SHIPPING);
            case SHIPPING -> EnumSet.of(OrderStatus.COMPLETED);
            case COMPLETED, CANCELLED -> EnumSet.noneOf(OrderStatus.class);
        };
        return allowedTargets.contains(to);
    }

    private boolean isAllowedStatusForReimport(OrderStatus status) {
        return status == OrderStatus.PENDING_QUOTE
                || status == OrderStatus.PENDING_APPROVAL
                || status == OrderStatus.CANCELLED;
    }

    /**
     * Admin: Delay delivery — update delivery_date + send customer email
     */
    @Transactional
    public OrderResponseDTO delayDelivery(Long orderId, DelayDeliveryRequestDTO request,
                                          org.example.features.auth.service.EmailService emailService) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        order.setDeliveryDate(request.getNewDeliveryDate());
        Order saved = orderRepository.save(order);

        // Notify customer async
        try {
            if (saved.getUser() != null && saved.getUser().getEmail() != null) {
                emailService.sendDelayNotification(
                        saved.getUser(),
                        saved.getOrderNumber(),
                        request.getNewDeliveryDate(),
                        request.getReason());
            }
        } catch (Exception e) {
            log.warn("Could not send delay notification email for order {}: {}", saved.getOrderNumber(), e.getMessage());
        }

        log.info("Order {} delivery date updated to {} (reason: {})",
                saved.getOrderNumber(), request.getNewDeliveryDate(), request.getReason());
        return mapToDTO(saved);
    }

    /**
     * Admin: Mark order as SHIPPING (handed to carrier)
     */
    @Transactional
    public OrderResponseDTO shipOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (order.getStatus() != OrderStatus.AWAITING_DELIVERY) {
            throw new IllegalStateException(
                    "Chỉ có thể giao khi đơn đang ở trạng thái Chờ giao hàng. Trạng thái hiện tại: " + order.getStatus());
        }

        order.setStatus(OrderStatus.SHIPPING);
        order.setShippedAt(LocalDateTime.now());
        order.setCompletedAt(null);
        Order saved = orderRepository.save(order);

        notifyOrderStatusTransition(saved, OrderStatus.AWAITING_DELIVERY, OrderStatus.SHIPPING);
        orderAuditService.recordStatusEvent(
            saved,
            OrderEventType.STATUS_CHANGED,
            OrderStatus.AWAITING_DELIVERY,
            OrderStatus.SHIPPING,
            null,
            "ADMIN",
            "Admin bàn giao đơn vị vận chuyển");

        log.info("Order {} is now SHIPPING", saved.getOrderNumber());
        return mapToDTO(saved);
    }

    /**
     * Admin: Mark order as COMPLETED (delivered successfully)
     */
    @Transactional
    public OrderResponseDTO completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus != OrderStatus.SHIPPING && currentStatus != OrderStatus.AWAITING_DELIVERY) {
            throw new IllegalStateException(
                    "Chỉ có thể hoàn thành khi đơn đang giao hoặc chờ giao. Trạng thái hiện tại: " + order.getStatus());
        }

        order.setStatus(OrderStatus.COMPLETED);
        if (order.getShippedAt() == null && currentStatus == OrderStatus.AWAITING_DELIVERY) {
            order.setShippedAt(LocalDateTime.now());
        }
        order.setCompletedAt(LocalDateTime.now());
        Order saved = orderRepository.save(order);

        notifyOrderStatusTransition(saved, currentStatus, OrderStatus.COMPLETED);
        orderAuditService.recordStatusEvent(
            saved,
            OrderEventType.STATUS_CHANGED,
            currentStatus,
            OrderStatus.COMPLETED,
            null,
            "ADMIN",
            "Admin xác nhận đơn hoàn thành");

        log.info("Order {} COMPLETED", saved.getOrderNumber());
        return mapToDTO(saved);
    }

    /**
     * Customer: Confirm item receipt for SHIPPING order.
     * SHIPPING -> COMPLETED immediately.
     */
    @Transactional
    public OrderResponseDTO confirmReceivedByCustomer(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new SecurityException("Bạn không có quyền xác nhận đơn hàng này");
        }

        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new IllegalStateException(
                    "Chỉ có thể xác nhận đã nhận khi đơn đang ở trạng thái ĐANG GIAO. Trạng thái hiện tại: " + order.getStatus());
        }

        Optional<OrderComplaint> activeComplaint = orderComplaintRepository.findByOrderIdAndUserId(orderId, userId);
        if (activeComplaint.isPresent()) {
            ComplaintStatus cStatus = activeComplaint.get().getStatus();
            if (cStatus == ComplaintStatus.OPEN || cStatus == ComplaintStatus.IN_REVIEW) {
                throw new IllegalStateException("Đơn hàng đang có khiếu nại (Mã KN-" + activeComplaint.get().getId() + ") chưa được giải quyết xong. Bạn không thể xác nhận nhận hàng lúc này.");
            }
        }

        order.setStatus(OrderStatus.COMPLETED);
        if (order.getShippedAt() == null) {
            order.setShippedAt(LocalDateTime.now());
        }
        order.setCompletedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        notifyOrderStatusTransition(saved, OrderStatus.SHIPPING, OrderStatus.COMPLETED);
        orderAuditService.recordStatusEvent(
            saved,
            OrderEventType.CUSTOMER_CONFIRMED_RECEIVED,
            OrderStatus.SHIPPING,
            OrderStatus.COMPLETED,
            userId,
            "CUSTOMER",
            "Khách hàng xác nhận đã nhận hàng");

        log.info("Order {} confirmed received by customer {}", saved.getOrderNumber(), userId);
        return mapToDTO(saved);
    }

    /**
     * Map Order entity to DTO
     */
    private OrderResponseDTO mapImportBatchToDTO(OrderImportBatch batch) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(batch.getId());
        dto.setOrderNumber(batch.getImportCode());
        dto.setUserId(batch.getUser() != null ? batch.getUser().getId() : null);
        dto.setUserName(batch.getUser() != null ? batch.getUser().getFullName() : null);
        dto.setCompanyId(batch.getCompany() != null ? batch.getCompany().getId() : null);
        dto.setCompanyName(batch.getCompany() != null ? batch.getCompany().getCompanyName() : null);
        if (batch.getStatus() == ImportBatchStatus.REJECTED) {
            dto.setStatus(OrderStatus.CANCELLED);
        } else if (batch.getStatus() == ImportBatchStatus.APPROVED && batch.getApprovedOrder() != null) {
            dto.setStatus(batch.getApprovedOrder().getStatus());
        } else {
            dto.setStatus(OrderStatus.PENDING_APPROVAL);
        }
        dto.setOrderType(OrderType.CUSTOM_MANUFACTURING);
        dto.setTotalPrice(null);
        dto.setDepositAmount(null);
        dto.setPaymentQrUrl(null);
        dto.setPaidAt(null);
        if (batch.getStatus() == ImportBatchStatus.REJECTED) {
            dto.setNotes("Đơn import đã bị từ chối");
            dto.setCancelReason(batch.getRejectionReason());
            dto.setCancelledByRole(batch.getRejectedByRole());
            dto.setRejectedByAdmin(batch.getRejectedByRole() != null
                    && "ADMIN".equalsIgnoreCase(batch.getRejectedByRole()));
        } else {
            dto.setNotes("Đơn import đang chờ admin duyệt");
            dto.setCancelReason(null);
            dto.setCancelledByRole(null);
            dto.setRejectedByAdmin(false);
        }
        dto.setDeliveryDate(null);
        dto.setPaymentDeadline(null);
        dto.setCreatedAt(batch.getCreatedAt());
        dto.setUpdatedAt(batch.getUpdatedAt());

        List<OrderItemDTO> itemDTOs = batch.getItems().stream().map(item -> {
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
            itemDTO.setReviewStatus(ItemReviewStatus.PENDING_REVIEW.name());
            itemDTO.setDeliveryDate(item.getDeliveryDate() != null ? item.getDeliveryDate().toString() : null);
            return itemDTO;
        }).toList();
        dto.setItems(itemDTOs);

        return dto;
    }

    private OrderResponseDTO mapToDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setUserName(order.getUser() != null ? order.getUser().getFullName() : null);
        dto.setCompanyId(order.getCompany() != null ? order.getCompany().getId() : null);
        dto.setCompanyName(order.getCompany() != null ? order.getCompany().getCompanyName() : null);
        dto.setStatus(order.getStatus());
        dto.setOrderType(order.getOrderType());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setDepositAmount(order.getDepositAmount());
        dto.setPaymentQrUrl(order.getPaymentQrUrl());
        dto.setPaidAt(order.getPaidAt());
        dto.setShippedAt(order.getShippedAt());
        dto.setCompletedAt(order.getCompletedAt());
        dto.setNotes(order.getNotes());
        dto.setCancelReason(null);
        dto.setCancelledByRole(null);
        dto.setRejectedByAdmin(false);
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setPaymentDeadline(order.getPaymentDeadline());
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
            // Delivery date
            itemDTO.setDeliveryDate(item.getDeliveryDate() != null ? item.getDeliveryDate().toString() : null);
            return itemDTO;
        }).collect(Collectors.toList());
        dto.setItems(itemDTOs);
        attachCancelMetadata(order, dto);

        return dto;
    }

    private void attachCancelMetadata(Order order, OrderResponseDTO dto) {
        if (order == null || dto == null || order.getStatus() != OrderStatus.CANCELLED) {
            return;
        }

        OrderEvent latestCancelEvent = orderEventRepository
                .findTopByOrderIdAndEventTypeOrderByCreatedAtDescIdDesc(order.getId(), OrderEventType.ORDER_CANCELLED)
                .orElse(null);

        if (latestCancelEvent == null) {
            return;
        }

        String actorRole = latestCancelEvent.getActorRole();
        dto.setCancelReason(latestCancelEvent.getNote());
        dto.setCancelledByRole(actorRole);
        dto.setRejectedByAdmin(actorRole != null && "ADMIN".equalsIgnoreCase(actorRole));
    }
}
