package org.example.features.product;

import org.example.features.product.dto.ProductFilterReq;
import org.example.features.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @ModelAttribute ProductFilterReq filter) { // <-- Gom gọn tất cả vào đây

        // Xử lý logic sắp xếp từ object filter
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("price_asc".equals(filter.getSortType())) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(filter.getSortType())) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize(), sort);

        // Truyền thẳng filter xuống Service để truy vấn
        Page<ProductResponse> result = productService.getProducts(filter, pageable);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
}