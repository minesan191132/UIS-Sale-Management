package org.example.features.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.example.features.warehouse.entity.DrawingCategory;
import org.example.features.warehouse.service.QuotePricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse/categories")
@RequiredArgsConstructor
public class QuotePricingController {

    private final QuotePricingService quotePricingService;

    @GetMapping
    public ResponseEntity<List<DrawingCategory>> getAllCategories(
            @RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(quotePricingService.getCategoriesByType(category));
        }
        return ResponseEntity.ok(quotePricingService.getAllCategories());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DrawingCategory>> searchDrawings(@RequestParam String q) {
        return ResponseEntity.ok(quotePricingService.searchDrawings(q));
    }

    @GetMapping("/by-drawing/{drawingNumber}")
    public ResponseEntity<Map<String, Object>> getDefaultPrice(@PathVariable String drawingNumber) {
        BigDecimal price = quotePricingService.getDefaultPriceForDrawing(drawingNumber);
        return ResponseEntity.ok(Map.of(
                "drawingNumber", drawingNumber,
                "defaultPrice", price != null ? price : 0
        ));
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<DrawingCategory> updatePrice(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        BigDecimal newPrice = new BigDecimal(body.get("defaultPrice").toString());
        return ResponseEntity.ok(quotePricingService.updatePrice(id, newPrice));
    }

    @PostMapping("/upsert-price")
    public ResponseEntity<DrawingCategory> upsertPrice(@RequestBody Map<String, Object> body) {
        String drawingNumber = (String) body.get("drawingNumber");
        String category = (String) body.get("category");
        BigDecimal price = new BigDecimal(body.get("defaultPrice").toString());
        return ResponseEntity.ok(quotePricingService.upsertPrice(drawingNumber, category, price));
    }
}
