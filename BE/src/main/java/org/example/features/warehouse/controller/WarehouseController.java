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



    @GetMapping("/companies")
    public ResponseEntity<List<String>> getCompanyNames() {
        return ResponseEntity.ok(warehouseService.getCompanyNames());
    }
}
