package org.example.features.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.example.features.warehouse.entity.DrawingCategory;
import org.example.features.warehouse.repository.DrawingCategoryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotePricingService {

    private final DrawingCategoryRepository drawingCategoryRepository;

    public BigDecimal getDefaultPriceForDrawing(String drawingNumber) {
        return drawingCategoryRepository.findByDrawingNumber(drawingNumber)
                .map(DrawingCategory::getDefaultPrice)
                .orElse(null);
    }

    public List<DrawingCategory> getAllCategories() {
        return drawingCategoryRepository.findAll();
    }

    public List<DrawingCategory> getCategoriesByType(String category) {
        return drawingCategoryRepository.findByCategory(category);
    }

    public List<DrawingCategory> searchDrawings(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return drawingCategoryRepository.findAll();
        }
        return drawingCategoryRepository.findByDrawingNumberContainingIgnoreCase(keyword.trim());
    }

    public DrawingCategory updatePrice(Long categoryId, BigDecimal newPrice) {
        DrawingCategory category = drawingCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.setDefaultPrice(newPrice);
        return drawingCategoryRepository.save(category);
    }

    public DrawingCategory upsertPrice(String drawingNumber, String category, BigDecimal price) {
        return drawingCategoryRepository.findByDrawingNumber(drawingNumber)
                .map(existing -> {
                    existing.setDefaultPrice(price);
                    return drawingCategoryRepository.save(existing);
                })
                .orElseGet(() -> {
                    DrawingCategory newCat = new DrawingCategory();
                    newCat.setDrawingNumber(drawingNumber);
                    newCat.setCategory(category != null ? category : "GIA_CONG");
                    newCat.setDefaultPrice(price);
                    return drawingCategoryRepository.save(newCat);
                });
    }
}
