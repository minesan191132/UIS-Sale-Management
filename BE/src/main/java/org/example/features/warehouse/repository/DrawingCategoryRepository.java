package org.example.features.warehouse.repository;

import org.example.features.warehouse.entity.DrawingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrawingCategoryRepository extends JpaRepository<DrawingCategory, Long> {
    Optional<DrawingCategory> findByDrawingNumber(String drawingNumber);
    List<DrawingCategory> findByCategory(String category);
    List<DrawingCategory> findByDrawingNumberContainingIgnoreCase(String keyword);
}
