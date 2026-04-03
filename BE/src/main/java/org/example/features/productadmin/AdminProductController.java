package org.example.features.productadmin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.productadmin.dto.AdminProductRequest;
import org.example.features.productadmin.dto.AdminProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/productadmin")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final AdminProductService adminProductService;

    /**
     * GET /api/productadmin/stats
     * Thống kê sản phẩm: tổng, đang hoạt động, hết hàng, sắp hết hàng
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(adminProductService.getStats());
    }

    /**
     * GET /api/productadmin
     * Danh sách sản phẩm có phân trang, tìm kiếm, lọc
     */
    @GetMapping
    public ResponseEntity<Page<AdminProductResponse>> getAll(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(adminProductService.getAll(keyword, categoryId, status, pageable));
    }

    /**
     * GET /api/productadmin/{id}
     * Chi tiết 1 sản phẩm
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminProductService.getById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/productadmin
     * Thêm sản phẩm mới
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AdminProductRequest req) {
        try {
            AdminProductResponse created = adminProductService.create(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating product", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * PUT /api/productadmin/{id}
     * Cập nhật sản phẩm
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody AdminProductRequest req) {
        try {
            return ResponseEntity.ok(adminProductService.update(id, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating product id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * DELETE /api/productadmin/{id}
     * Xoá mềm (soft delete: isActive = false)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            adminProductService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Đã xoá sản phẩm thành công"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting product id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
