package org.example.features.warehouse.repository;

import org.example.features.warehouse.entity.DrawingMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrawingMetaRepository extends JpaRepository<DrawingMeta, Long> {
    Optional<DrawingMeta> findByDrawingNumber(String drawingNumber);

    List<DrawingMeta> findByDrawingNumberIn(List<String> drawingNumbers);
}
