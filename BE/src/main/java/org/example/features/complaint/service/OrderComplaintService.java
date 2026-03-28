package org.example.features.complaint.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.UserRepository;
import org.example.features.complaint.dto.ComplaintMissingItemInputDTO;
import org.example.features.complaint.dto.OrderComplaintImageResponseDTO;
import org.example.features.complaint.dto.OrderComplaintItemResponseDTO;
import org.example.features.complaint.dto.OrderComplaintResponseDTO;
import org.example.features.complaint.entity.ComplaintStatus;
import org.example.features.complaint.entity.OrderComplaint;
import org.example.features.complaint.entity.OrderComplaintImage;
import org.example.features.complaint.entity.OrderComplaintItem;
import org.example.features.complaint.repository.OrderComplaintRepository;
import org.example.features.notification.entity.NotificationType;
import org.example.features.notification.service.UserNotificationService;
import org.example.features.order.entity.Order;
import org.example.features.order.entity.OrderItem;
import org.example.features.order.entity.OrderStatus;
import org.example.features.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderComplaintService {

    private static final int MAX_IMAGES = 5;
    private static final long MAX_IMAGE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");

    private final OrderRepository orderRepository;
    private final OrderComplaintRepository orderComplaintRepository;
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
                                                       String description,
                                                       String missingItemsJson,
                                                       List<Long> keepImageIds,
                                                       List<MultipartFile> images) {
        Order order = loadOwnedOrder(orderId, userId);
        ensureShippingOrder(order);

        String normalizedDescription = normalizeDescription(description);
        List<ComplaintMissingItemInputDTO> missingInputs = parseMissingItems(missingItemsJson);

        OrderComplaint complaint = orderComplaintRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseGet(OrderComplaint::new);
        boolean isNewComplaint = complaint.getId() == null;

        complaint.setOrder(order);
        complaint.setUser(order.getUser());
        complaint.setDescription(normalizedDescription);
        complaint.setStatus(ComplaintStatus.OPEN);

        replaceMissingItems(complaint, order, missingInputs);
        reconcileExistingImages(complaint, keepImageIds);

        OrderComplaint persisted = orderComplaintRepository.save(complaint);
        addNewImages(persisted, images);

        if (persisted.getImages().isEmpty()) {
            throw new IllegalArgumentException("Khiếu nại cần có ít nhất 1 ảnh minh chứng.");
        }

        OrderComplaint saved = orderComplaintRepository.save(persisted);
        pushComplaintNotifications(saved, isNewComplaint);

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
        if (order.getStatus() != OrderStatus.SHIPPING) {
            throw new IllegalStateException("Chỉ có thể khiếu nại khi đơn đang ở trạng thái ĐANG GIAO");
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập mô tả khiếu nại");
        }
        return description.trim();
    }

    private List<ComplaintMissingItemInputDTO> parseMissingItems(String missingItemsJson) {
        if (missingItemsJson == null || missingItemsJson.isBlank()) {
            throw new IllegalArgumentException("Vui lòng cung cấp chi tiết số lượng thiếu");
        }

        try {
            List<ComplaintMissingItemInputDTO> parsed = objectMapper.readValue(
                    missingItemsJson,
                    new TypeReference<List<ComplaintMissingItemInputDTO>>() {});

            if (parsed == null || parsed.isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn ít nhất 1 sản phẩm thiếu");
            }

            return parsed;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Dữ liệu sản phẩm thiếu không hợp lệ");
        }
    }

    private void replaceMissingItems(OrderComplaint complaint,
                                     Order order,
                                     List<ComplaintMissingItemInputDTO> missingInputs) {
        Map<Long, OrderItem> orderItemsById = order.getItems().stream()
                .collect(Collectors.toMap(OrderItem::getId, item -> item));

        Map<Long, Integer> normalizedByItem = new LinkedHashMap<>();
        for (ComplaintMissingItemInputDTO input : missingInputs) {
            if (input == null || input.getOrderItemId() == null || input.getMissingQuantity() == null) {
                throw new IllegalArgumentException("Chi tiết số lượng thiếu không hợp lệ");
            }

            int missingQuantity = input.getMissingQuantity();
            if (missingQuantity <= 0) {
                continue;
            }

            OrderItem orderItem = orderItemsById.get(input.getOrderItemId());
            if (orderItem == null) {
                throw new IllegalArgumentException("Sản phẩm khiếu nại không thuộc đơn hàng hiện tại");
            }

            int orderedQty = orderItem.getQuantity() == null ? 0 : orderItem.getQuantity();
            if (missingQuantity > orderedQty) {
                throw new IllegalArgumentException("Số lượng thiếu không được lớn hơn số lượng đã đặt");
            }

            normalizedByItem.put(orderItem.getId(), missingQuantity);
        }

        if (normalizedByItem.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng nhập số lượng thiếu cho ít nhất 1 sản phẩm");
        }

        complaint.getMissingItems().clear();
        normalizedByItem.forEach((orderItemId, missingQty) -> {
            OrderComplaintItem complaintItem = new OrderComplaintItem();
            complaintItem.setOrderItem(orderItemsById.get(orderItemId));
            complaintItem.setMissingQuantity(missingQty);
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
                    "Đơn " + order.getOrderNumber() + " có khiếu nại thiếu hàng từ khách hàng.",
                    "complaint-admin-" + complaint.getId() + "-" + admin.getId() + "-" + System.currentTimeMillis());
        }
    }

    private OrderComplaintResponseDTO toDTO(OrderComplaint complaint) {
        OrderComplaintResponseDTO dto = new OrderComplaintResponseDTO();
        dto.setId(complaint.getId());
        dto.setOrderId(complaint.getOrder() != null ? complaint.getOrder().getId() : null);
        dto.setOrderNumber(complaint.getOrder() != null ? complaint.getOrder().getOrderNumber() : null);
        dto.setStatus(complaint.getStatus());
        dto.setDescription(complaint.getDescription());
        dto.setCreatedAt(complaint.getCreatedAt());
        dto.setUpdatedAt(complaint.getUpdatedAt());

        List<OrderComplaintItemResponseDTO> missingItemDTOs = complaint.getMissingItems().stream().map(item -> {
            OrderComplaintItemResponseDTO itemDTO = new OrderComplaintItemResponseDTO();
            itemDTO.setOrderItemId(item.getOrderItem() != null ? item.getOrderItem().getId() : null);
            itemDTO.setItemCode(item.getOrderItem() != null ? item.getOrderItem().getItemCode() : null);
            itemDTO.setItemName(item.getOrderItem() != null ? item.getOrderItem().getItemName() : null);
            itemDTO.setOrderedQuantity(item.getOrderItem() != null ? item.getOrderItem().getQuantity() : null);
            itemDTO.setMissingQuantity(item.getMissingQuantity());
            return itemDTO;
        }).toList();

        List<OrderComplaintImageResponseDTO> imageDTOs = complaint.getImages().stream().map(image -> {
            OrderComplaintImageResponseDTO imageDTO = new OrderComplaintImageResponseDTO();
            imageDTO.setId(image.getId());
            imageDTO.setImageUrl(image.getImageUrl());
            imageDTO.setOriginalFilename(image.getOriginalFilename());
            return imageDTO;
        }).toList();

        dto.setMissingItems(missingItemDTOs);
        dto.setImages(imageDTOs);
        return dto;
    }
}
