package org.example.features.product;

import org.example.features.product.dto.ProductFilterReq;
import org.example.features.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductPageRepository productPageRepository;

    // Đã sửa tham số nhận vào thành ProductFilterReq để khớp với Controller
    public Page<ProductResponse> getProducts(ProductFilterReq filter, Pageable pageable) {

        String rawKeyword = filter.getKeyword();
        String safeKeyword = (rawKeyword == null || rawKeyword.trim().isEmpty())
                ? ""
                : rawKeyword.trim().toLowerCase();

        // Bóc tách các giá trị từ filter (có thể bị null) và truyền vào query
        Page<Product> products = productPageRepository.filterProducts(
                filter.getCategoryId(),
                safeKeyword,
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getInStockOnly(),
                pageable
        );

        // Chuyển đổi từ Entity sang DTO
        return products.map(this::mapToResponse);
    }

    private ProductResponse mapToResponse(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStockQuantity(product.getStockQuantity());

        // THÊM DÒNG NÀY ĐỂ TRUYỀN MÔ TẢ TỪ DATABASE LÊN WEB
        dto.setDescription(product.getDescription());

        // Xử lý logic: Nếu số lượng > 0 là Còn hàng
        dto.setAvailable(product.getStockQuantity() != null && product.getStockQuantity() > 0);

        dto.setDefaultMaterial(product.getDefaultMaterial());
        dto.setDefaultSpecification(product.getDefaultSpecification());
        return dto;
    }

    public ProductResponse getProductById(Long id) {
        Product product = productPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        return mapToResponse(product);
    }
}