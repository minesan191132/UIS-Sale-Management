package org.example.features.productadmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.productadmin.dto.AdminProductRequest;
import org.example.features.productadmin.dto.AdminProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminProductService {

    private final AdminProductRepository adminProductRepository;

    public Page<AdminProductResponse> getAll(String keyword, Long categoryId, String status, Pageable pageable) {
        return adminProductRepository.findByFilters(keyword, categoryId, status, pageable)
                .map(this::toResponse);
    }

    public AdminProductResponse getById(Long id) {
        AdminProduct p = adminProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));
        return toResponse(p);
    }

    @Transactional
    public AdminProductResponse create(AdminProductRequest req) {
        AdminProduct p = new AdminProduct();
        applyFields(p, req);
        return toResponse(adminProductRepository.save(p));
    }

    @Transactional
    public AdminProductResponse update(Long id, AdminProductRequest req) {
        AdminProduct p = adminProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));
        applyFields(p, req);
        return toResponse(adminProductRepository.save(p));
    }

    @Transactional
    public void delete(Long id) {
        AdminProduct p = adminProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));
        p.setIsActive(false);
        adminProductRepository.save(p);
        log.info("Soft-deleted product id={}", id);
    }

    /**
     * Trừ tồn kho khi đơn hàng được xác nhận thanh toán.
     * Match sản phẩm theo tên (case-insensitive).
     * Nếu không tìm thấy sản phẩm hoặc không đủ hàng → bỏ qua (log warning).
     */
    @Transactional
    public void deductStock(String productName, int quantity) {
        if (productName == null || productName.isBlank()) return;

        AdminProduct product = adminProductRepository.findByNameIgnoreCase(productName.trim());
        if (product == null) {
            throw new IllegalArgumentException("Hệ thống không tìm thấy sản phẩm: " + productName);
        }

        int current = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        if (current == 0) {
            throw new IllegalArgumentException("Sản phẩm '" + productName + "' đã hết hàng!");
        }
        if (current < quantity) {
            throw new IllegalArgumentException("Sản phẩm '" + productName + "' chỉ còn " + current + " sản phẩm. Vui lòng giảm số lượng!");
        }

        int newQty = current - quantity;
        product.setStockQuantity(newQty);
        adminProductRepository.save(product);
        log.info("deductStock: '{}' {} → {} (trừ {})", productName, current, newQty, quantity);
    }

    public Map<String, Long> getStats() {
        long total = adminProductRepository.count();
        long active = adminProductRepository.countByIsActiveTrue();
        long outOfStock = adminProductRepository.countOutOfStock();
        long lowStock = adminProductRepository.countLowStock();
        return Map.of(
                "total", total,
                "active", active,
                "outOfStock", outOfStock,
                "lowStock", lowStock
        );
    }

    private void applyFields(AdminProduct p, AdminProductRequest req) {
        if (req.getName() != null) p.setName(req.getName());
        if (req.getSku() != null) p.setSku(req.getSku());
        if (req.getCategoryId() != null) p.setCategoryId(req.getCategoryId());
        if (req.getPrice() != null) p.setPrice(req.getPrice());
        if (req.getDescription() != null) p.setDescription(req.getDescription());
        if (req.getImageUrl() != null) p.setImageUrl(req.getImageUrl());
        if (req.getStockQuantity() != null) p.setStockQuantity(req.getStockQuantity());
        if (req.getIsActive() != null) p.setIsActive(req.getIsActive());
        if (req.getDefaultMaterial() != null) p.setDefaultMaterial(req.getDefaultMaterial());
        if (req.getDefaultSpecification() != null) p.setDefaultSpecification(req.getDefaultSpecification());

        // Chỉ ghi slug nếu được cung cấp và không rỗng
        if (req.getSlug() != null && !req.getSlug().isBlank()) {
            p.setSlug(req.getSlug());
        } else if (p.getSlug() == null || p.getSlug().isBlank()) {
            // Auto-generate slug từ tên + timestamp để đảm bảo unique
            String baseName = p.getName() != null ? p.getName() : "product";
            p.setSlug(generateSlug(baseName) + "-" + System.currentTimeMillis());
        }
        // Nếu slug đã có giá trị hợp lệ → giữ nguyên

        p.setUpdatedAt(LocalDateTime.now());
    }

    /**
     * Chuyển tên thành slug URL-safe (viết thường, loại bỏ ký tự đặc biệt)
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }


    private AdminProductResponse toResponse(AdminProduct p) {
        AdminProductResponse r = new AdminProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setSku(p.getSku());
        r.setCategoryId(p.getCategoryId());
        r.setPrice(p.getPrice());
        r.setDescription(p.getDescription());
        r.setImageUrl(p.getImageUrl());
        r.setStockQuantity(p.getStockQuantity());
        r.setIsActive(p.getIsActive());
        r.setDefaultMaterial(p.getDefaultMaterial());
        r.setDefaultSpecification(p.getDefaultSpecification());
        r.setSlug(p.getSlug());
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());

        int qty = p.getStockQuantity() != null ? p.getStockQuantity() : 0;
        if (qty == 0) r.setStatus("Hết hàng");
        else if (qty <= 10) r.setStatus("Sắp hết hàng");
        else r.setStatus("Còn hàng");

        return r;
    }
}
