package org.example.features.complaint.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.UserRepository;
import org.example.features.complaint.dto.ComplaintItemInputDTO;
import org.example.features.complaint.dto.OrderComplaintHistoryResponseDTO;
import org.example.features.complaint.dto.OrderComplaintImageResponseDTO;
import org.example.features.complaint.dto.OrderComplaintItemResponseDTO;
import org.example.features.complaint.dto.OrderComplaintResponseDTO;
import org.example.features.complaint.entity.ComplaintStatus;
import org.example.features.complaint.entity.ComplaintType;
import org.example.features.complaint.entity.OrderComplaint;
import org.example.features.complaint.entity.OrderComplaintHistory;
import org.example.features.complaint.entity.OrderComplaintImage;
import org.example.features.complaint.entity.OrderComplaintItem;
import org.example.features.complaint.repository.OrderComplaintHistoryRepository;
import org.example.features.complaint.repository.OrderComplaintRepository;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderComplaintService {

    private static final int MAX_IMAGES = 5;
    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    private final OrderRepository orderRepository;
    private final OrderComplaintRepository orderComplaintRepository;
    private final OrderComplaintHistoryRepository orderComplaintHistoryRepository;
    private final UserRepository userRepository;
    private final UserNotificationService userNotificationService;
    private final ObjectMapper objectMapper;

    @Value("${complaint.upload.dir:uploads/order-complaints}")
    private String complaintUploadDir;

    @Transactional(readOnly = true)
    public OrderComplaintResponseDTO getMyComplaintByOrder(Long orderId, Long userId) {
        loadOwnedOrder(orderId, userId);
        return orderComplaintRepository.findByOrderIdAndUserId(orderId, userId)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public OrderComplaintResponseDTO upsertMyComplaint(Long orderId,
                                                       Long userId,
                                                       ComplaintType type,
                                                       String description,
                                                       String complaintItemsJson,
                                                       List<Long> keepImageIds,
                                                       List<MultipartFile> images) {
        Order order = loadOwnedOrder(orderId, userId);
        ensureShippingOrder(order);

        String normalizedDescription = normalizeDescription(description);
        List<ComplaintItemInputDTO> inputs = parseComplaintItems(complaintItemsJson);

        OrderComplaint complaint = orderComplaintRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseGet(OrderComplaint::new);
        boolean isNewComplaint = complaint.getId() == null;

        String oldStatusStr = !isNewComplaint ? complaint.getStatus().name() : null;

        complaint.setOrder(order);
        complaint.setUser(order.getUser());
        complaint.setType(type != null ? type : ComplaintType.MISSING_ITEM);
        complaint.setDescription(normalizedDescription);
        complaint.setStatus(ComplaintStatus.OPEN);

        replaceComplaintItems(complaint, order, inputs);
        reconcileExistingImages(complaint, keepImageIds);

        OrderComplaint persisted = orderComplaintRepository.save(complaint);
        addNewImages(persisted, images);

        if (persisted.getImages().isEmpty()) {
            throw new IllegalArgumentException("Khiếu nại cần có ít nhất 1 ảnh minh chứng.");
        }

        OrderComplaint saved = orderComplaintRepository.save(persisted);
        pushComplaintNotifications(saved, isNewComplaint);

        // Save history event
        saveHistory(saved, userId, isNewComplaint ? "CREATED" : "UPDATED", oldStatusStr, ComplaintStatus.OPEN.name(), "Khách hàng cập nhật khiếu nại");

        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<OrderComplaintResponseDTO> adminGetAllComplaints(int page, int size, String keyword, ComplaintStatus status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending().and(Sort.by("createdAt").descending()));
        
        Page<OrderComplaint> result;
        if (keyword != null && !keyword.trim().isEmpty() && status != null) {
            result = orderComplaintRepository.findByOrder_OrderNumberContainingIgnoreCaseAndStatus(keyword.trim(), status, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            result = orderComplaintRepository.findByOrder_OrderNumberContainingIgnoreCase(keyword.trim(), pageable);
        } else if (status != null) {
            result = orderComplaintRepository.findByStatus(status, pageable);
        } else {
            result = orderComplaintRepository.findAll(pageable);
        }

        return result.map(this::toDTO);
    }

    @Transactional
    public OrderComplaintResponseDTO adminUpdateComplaintStatus(Long complaintId, ComplaintStatus newStatus, String adminNote, Long adminId) {
        OrderComplaint complaint = orderComplaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khiếu nại"));

        ComplaintStatus oldStatus = complaint.getStatus();
        complaint.setStatus(newStatus);
        
        if (adminNote != null && !adminNote.trim().isEmpty()) {
            complaint.setAdminNote(adminNote.trim());
        }

        OrderComplaint saved = orderComplaintRepository.save(complaint);

        String action = oldStatus == newStatus ? "ADMIN_REPLY" : "STATUS_CHANGED";
        saveHistory(saved, adminId, action, oldStatus.name(), newStatus.name(), adminNote);

        // Notify customer
        if (oldStatus != newStatus || (adminNote != null && !adminNote.isEmpty())) {
            Order order = saved.getOrder();
            userNotificationService.pushNotificationToUser(
                    saved.getUser().getId(),
                    order,
                    NotificationType.COMPLAINT,
                    "Cập nhật khiếu nại đơn " + order.getOrderNumber(),
                    "Admin đã xử lý khiếu nại của bạn: " + newStatus.name(),
                    "complaint-update-" + complaint.getId() + "-" + System.currentTimeMillis());
        }

        return toDTO(saved);
    }


    private Order loadOwnedOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (order.getUser() == null || !Objects.equals(order.getUser().getId(), userId)) {
            throw new SecurityException("Bạn không có quyền thao tác trên đơn hàng này");
        }

        return order;
    }

    private void ensureShippingOrder(Order order) {
        if (order.getStatus() != OrderStatus.SHIPPING && order.getStatus() != OrderStatus.COMPLETED) {
            throw new IllegalStateException("Chỉ có thể khiếu nại khi đơn đang ở trạng thái ĐANG GIAO hoặc HOÀN THÀNH");
        }
        if (order.getStatus() == OrderStatus.COMPLETED) {
            if (order.getCompletedAt() != null && order.getCompletedAt().isBefore(java.time.LocalDateTime.now().minusDays(7))) {
                throw new IllegalStateException("Chỉ có thể khiếu nại trong vòng 7 ngày kể từ khi xác nhận Hoàn thành đơn hàng.");
            }
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mô tả khiếu nại");
        }
        return description.trim();
    }

    private List<ComplaintItemInputDTO> parseComplaintItems(String missingItemsJson) {
        if (missingItemsJson == null || missingItemsJson.isBlank()) {
            throw new IllegalArgumentException("Vui lòng cung cấp chi tiết tình trạng sản phẩm");
        }

        try {
            List<ComplaintItemInputDTO> parsed = objectMapper.readValue(
                    missingItemsJson,
                    new TypeReference<List<ComplaintItemInputDTO>>() {});

            if (parsed == null || parsed.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 sản phẩm bị lỗi/thiếu");
            }

            return parsed;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Dữ liệu chi tiết sản phẩm không hợp lệ");
        }
    }

    private void replaceComplaintItems(OrderComplaint complaint,
                                     Order order,
                                     List<ComplaintItemInputDTO> inputs) {
        Map<Long, OrderItem> orderItemsById = order.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getId, item -> item));

        Map<Long, ComplaintItemInputDTO> normalizedByItem = new LinkedHashMap<>();
        for (ComplaintItemInputDTO input : inputs) {
            if (input == null || input.getOrderItemId() == null) {
                throw new IllegalArgumentException("Chi tiết sản phẩm không hợp lệ");
            }

            int missQty = input.getMissingQuantity() == null ? 0 : input.getMissingQuantity();
            int defQty = input.getDefectiveQuantity() == null ? 0 : input.getDefectiveQuantity();

            if (missQty < 0 || defQty < 0) {
                throw new IllegalArgumentException("Số lượng lỗi hoặc thiếu không được là số âm");
            }

            if (missQty == 0 && defQty == 0) {
                continue;
            }

            OrderItem orderItem = orderItemsById.get(input.getOrderItemId());
            if (orderItem == null) {
                throw new IllegalArgumentException("Sản phẩm khiếu nại không thuộc đơn hàng hiện tại");
            }

            int orderedQty = orderItem.getQuantity() == null ? 0 : orderItem.getQuantity();
            if (missQty + defQty > orderedQty) {
                throw new IllegalArgumentException("Tổng số lượng lỗi và thiếu không được lớn hơn số lượng đã đặt");
            }

            normalizedByItem.put(orderItem.getId(), input);
        }

        if (normalizedByItem.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập số lượng lỗi/thiếu cho ít nhất 1 sản phẩm");
        }

        Iterator<OrderComplaintItem> iterator = complaint.getMissingItems().iterator();
        while (iterator.hasNext()) {
            OrderComplaintItem existingItem = iterator.next();
            Long oId = existingItem.getOrderItem().getId();

            if (!normalizedByItem.containsKey(oId)) {
                iterator.remove();
            } else {
                ComplaintItemInputDTO data = normalizedByItem.get(oId);
                existingItem.setMissingQuantity(data.getMissingQuantity() == null ? 0 : data.getMissingQuantity());
                existingItem.setDefectiveQuantity(data.getDefectiveQuantity() == null ? 0 : data.getDefectiveQuantity());
                existingItem.setReasonNote(data.getReasonNote());
                
                normalizedByItem.remove(oId);
            }
        }

        normalizedByItem.forEach((orderItemId, data) -> {
            OrderComplaintItem complaintItem = new OrderComplaintItem();
            complaintItem.setOrderItem(orderItemsById.get(orderItemId));
            complaintItem.setMissingQuantity(data.getMissingQuantity() == null ? 0 : data.getMissingQuantity());
            complaintItem.setDefectiveQuantity(data.getDefectiveQuantity() == null ? 0 : data.getDefectiveQuantity());
            complaintItem.setReasonNote(data.getReasonNote());
            complaint.addMissingItem(complaintItem);
        });
    }

    private void reconcileExistingImages(OrderComplaint complaint, List<Long> keepImageIds) {
        Set<Long> keepIds = keepImageIds == null
                ? Set.of()
                : keepImageIds.stream().filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));

        if (complaint.getId() == null) {
            complaint.getImages().clear();
            return;
        }

        if (complaint.getImages() == null) {
            complaint.setImages(new ArrayList<>());
            return;
        }

        List<OrderComplaintImage> toDelete = complaint.getImages().stream()
                .filter(img -> img.getId() != null && !keepIds.contains(img.getId()))
                .toList();

        if (!toDelete.isEmpty()) {
            toDelete.forEach(img -> deleteStoredImageQuietly(img.getImageUrl()));
            complaint.getImages().removeAll(toDelete);
        }
    }

    private void addNewImages(OrderComplaint complaint, List<MultipartFile> images) {
        List<MultipartFile> uploadedImages = images == null
                ? List.of()
                : images.stream().filter(file -> file != null && !file.isEmpty()).toList();

        if (complaint.getImages().size() + uploadedImages.size() > MAX_IMAGES) {
            throw new IllegalArgumentException("Tối đa " + MAX_IMAGES + " ảnh minh chứng cho mỗi khiếu nại");
        }

        if (uploadedImages.isEmpty()) {
            return;
        }

        ensureUploadDirectoryExists();

        for (MultipartFile image : uploadedImages) {
            validateImageFile(image);
            String fileName = buildStoredFileName(complaint.getId(), image.getOriginalFilename());
            Path target = Paths.get(complaintUploadDir).resolve(fileName);

            try {
                Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Không thể lưu ảnh minh chứng: " + image.getOriginalFilename(), e);
            }

            OrderComplaintImage savedImage = new OrderComplaintImage();
            savedImage.setImageUrl("/uploads/order-complaints/" + fileName);
            savedImage.setOriginalFilename(image.getOriginalFilename());
            complaint.addImage(savedImage);
        }
    }

    private void ensureUploadDirectoryExists() {
        try {
            Files.createDirectories(Paths.get(complaintUploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục lưu ảnh khiếu nại", e);
        }
    }

    private void validateImageFile(MultipartFile image) {
        if (image.getSize() > MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException("Mỗi ảnh minh chứng tối đa 5MB");
        }

        String extension = getFileExtension(image.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP");
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException("Tệp tải lên phải là ảnh hợp lệ");
        }
    }

    private String buildStoredFileName(Long complaintId, String originalName) {
        String extension = getFileExtension(originalName);
        String safeExtension = ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) ? extension.toLowerCase() : ".jpg";
        return "complaint-" + complaintId + "-" + UUID.randomUUID() + safeExtension;
    }

    private String getFileExtension(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            return "";
        }
        int idx = originalName.lastIndexOf('.');
        if (idx < 0 || idx >= originalName.length() - 1) {
            return "";
        }
        return originalName.substring(idx);
    }

    private void deleteStoredImageQuietly(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String prefix = "/uploads/order-complaints/";
        if (!imageUrl.startsWith(prefix)) {
            return;
        }

        Path filePath = Paths.get(complaintUploadDir).resolve(imageUrl.substring(prefix.length()));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete old complaint image {}", filePath);
        }
    }

    private void saveHistory(OrderComplaint complaint, Long userId, String actionType, String oldStatus, String newStatus, String note) {
        OrderComplaintHistory history = new OrderComplaintHistory();
        history.setComplaint(complaint);
        history.setActionByUserId(userId);
        history.setActionType(actionType);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNote(note);
        orderComplaintHistoryRepository.save(history);
    }

    private void pushComplaintNotifications(OrderComplaint complaint, boolean isNewComplaint) {
        Order order = complaint.getOrder();
        String actionText = isNewComplaint ? "đã gửi" : "đã cập nhật";

        userNotificationService.pushNotificationToUser(
                complaint.getUser().getId(),
                order,
                NotificationType.COMPLAINT,
                isNewComplaint ? "Đã ghi nhận khiếu nại" : "Đã cập nhật khiếu nại",
                "Khiếu nại cho đơn " + order.getOrderNumber() + " " + actionText + " thành công.",
                "complaint-customer-" + complaint.getId() + "-" + System.currentTimeMillis());

        List<User> admins = userRepository.findByRole(UserRole.ADMIN).stream()
                .filter(admin -> Boolean.TRUE.equals(admin.getIsActive()))
                .toList();

        for (User admin : admins) {
            userNotificationService.pushNotificationToUser(
                    admin.getId(),
                    order,
                    NotificationType.COMPLAINT,
                    isNewComplaint ? "Có khiếu nại mới" : "Khiếu nại đã được cập nhật",
                    "Đơn " + order.getOrderNumber() + " có khiếu nại từ khách hàng.",
                    "complaint-admin-" + complaint.getId() + "-" + admin.getId() + "-" + System.currentTimeMillis());
        }
    }

    private OrderComplaintResponseDTO toDTO(OrderComplaint complaint) {
        OrderComplaintResponseDTO dto = new OrderComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setOrderId(complaint.getOrder() != null ? complaint.getOrder().getId() : null);
        dto.setOrderNumber(complaint.getOrder() != null ? complaint.getOrder().getOrderNumber() : null);
        dto.setStatus(complaint.getStatus());
        dto.setType(complaint.getType());
        dto.setAdminNote(complaint.getAdminNote());
        dto.setDescription(complaint.getDescription());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        List<OrderComplaintItemResponseDTO> itemDTOs = complaint.getMissingItems().stream().map(item -> {
            OrderComplaintItemResponseDTO itemDTO = new OrderComplaintItemResponseDTO();
            itemDTO.setOrderItemId(item.getOrderItem() != null ? item.getOrderItem().getId() : null);
            itemDTO.setItemCode(item.getOrderItem() != null ? item.getOrderItem().getItemCode() : null);
            itemDTO.setItemName(item.getOrderItem() != null ? item.getOrderItem().getItemName() : null);
            itemDTO.setOrderedQuantity(item.getOrderItem() != null ? item.getOrderItem().getQuantity() : null);
            itemDTO.setMissingQuantity(item.getMissingQuantity());
            itemDTO.setDefectiveQuantity(item.getDefectiveQuantity());
            itemDTO.setReasonNote(item.getReasonNote());
            return itemDTO;
        }).toList();

        List<OrderComplaintImageResponseDTO> imageDTOs = complaint.getImages().stream().map(image -> {
            OrderComplaintImageResponseDTO imageDTO = new OrderComplaintImageResponseDTO();
            imageDTO.setId(image.getId());
            imageDTO.setImageUrl(image.getImageUrl());
            imageDTO.setOriginalFilename(image.getOriginalFilename());
            return imageDTO;
        }).toList();

        dto.setMissingItems(itemDTOs);
        dto.setImages(imageDTOs);

        if (complaint.getId() != null) {
            List<OrderComplaintHistory> historyLogs = orderComplaintHistoryRepository.findByComplaintIdOrderByCreatedAtDesc(complaint.getId());
            List<OrderComplaintHistoryResponseDTO> historyDTOs = historyLogs.stream().map(h -> {
                OrderComplaintHistoryResponseDTO hdr = new OrderComplaintHistoryResponseDTO();
                hdr.setId(h.getId());
                hdr.setActionByUserId(h.getActionByUserId());
                hdr.setActionType(h.getActionType());
                hdr.setOldStatus(h.getOldStatus());
                hdr.setNewStatus(h.getNewStatus());
                hdr.setNote(h.getNote());
                hdr.setCreatedAt(h.getCreatedAt());
                if (h.getActionByUserId() != null) {
                    userRepository.findById(h.getActionByUserId()).ifPresent(u -> hdr.setActionByUserName(u.getFullName()));
                }
                return hdr;
            }).toList();
            dto.setHistory(historyDTOs);
        }

        return dto;
    }
}
