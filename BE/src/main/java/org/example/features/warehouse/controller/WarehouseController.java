package org.example.features.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.example.features.warehouse.dto.WarehouseItemDTO;
import org.example.features.warehouse.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<List<WarehouseItemDTO>> getWarehouseItems(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(warehouseService.getWarehouseItems(companyId, search));
    }

    @PutMapping("/meta/{drawingNumber}")
    public ResponseEntity<?> updateMeta(
            @PathVariable String drawingNumber,
            @RequestBody Map<String, Object> body) {
        BigDecimal weight = body.get("weight") != null
                ? new BigDecimal(body.get("weight").toString()) : null;
        Integer stock = body.get("stock") != null
                ? Integer.parseInt(body.get("stock").toString()) : null;

        warehouseService.updateMeta(drawingNumber, weight, stock);
        return ResponseEntity.ok(Map.of("message", "Updated successfully"));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<String>> getCompanyNames() {
        return ResponseEntity.ok(warehouseService.getCompanyNames());
    }
}
