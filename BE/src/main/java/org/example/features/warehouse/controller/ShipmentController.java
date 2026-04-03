package org.example.features.warehouse.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.features.warehouse.dto.ShipmentPreviewDTO;
import org.example.features.warehouse.service.ShipmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/preview")
    public ResponseEntity<ShipmentPreviewDTO> previewShipment(@RequestBody Map<String, Object> body) {
        try {
            ShipmentPreviewDTO preview = resolvePreview(body);
            return ResponseEntity.ok(preview);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }

    @PostMapping("/export-excel")
    public ResponseEntity<byte[]> exportExcel(@RequestBody Map<String, Object> body) {
        ShipmentPreviewDTO preview;
        try {
            preview = resolvePreview(body);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Phiếu xuất kho");

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            setBorders(headerStyle);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setBorders(cellStyle);

            CellStyle numberCenterStyle = workbook.createCellStyle();
            numberCenterStyle.cloneStyleFrom(cellStyle);
            numberCenterStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.cloneStyleFrom(headerStyle);

            List<OrderColumn> dynamicColumns = buildOrderColumns(preview);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("PHIEU XUAT KHO");
            titleCell.setCellStyle(titleStyle);

            Row infoRow = sheet.createRow(1);
            infoRow.createCell(0).setCellValue("Ma: " + safe(preview.getShipmentCode()));
            infoRow.createCell(2).setCellValue("Tieu de: " + safe(preview.getTitle()));

            int headerRowIdx = 3;
            Row mainHeaderRow = sheet.createRow(headerRowIdx);
            Row subHeaderRow = sheet.createRow(headerRowIdx + 1);

            String[] fixedHeaders = {"STT", "DRAWING NO", "PARTS NAME", "SPEC", "MATERIAL", "TONG"};
            for (int i = 0; i < fixedHeaders.length; i++) {
                Cell cell = mainHeaderRow.createCell(i);
                cell.setCellValue(fixedHeaders[i]);
                cell.setCellStyle(headerStyle);
                sheet.addMergedRegion(new CellRangeAddress(headerRowIdx, headerRowIdx + 1, i, i));
            }

            Map<String, Integer> groupSpan = new LinkedHashMap<>();
            for (OrderColumn col : dynamicColumns) {
                groupSpan.put(col.deliveryDate(), groupSpan.getOrDefault(col.deliveryDate(), 0) + 1);
            }

            int dynamicStartCol = fixedHeaders.length;
            int colIdx = dynamicStartCol;
            for (Map.Entry<String, Integer> e : groupSpan.entrySet()) {
                Cell groupCell = mainHeaderRow.createCell(colIdx);
                groupCell.setCellValue(formatDateForDisplay(e.getKey()));
                groupCell.setCellStyle(headerStyle);

                int span = e.getValue();
                if (span > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(headerRowIdx, headerRowIdx, colIdx, colIdx + span - 1));
                }
                colIdx += span;
            }

            for (int i = 0; i < dynamicColumns.size(); i++) {
                Cell cell = subHeaderRow.createCell(dynamicStartCol + i);
                cell.setCellValue(formatVnnShort(dynamicColumns.get(i).vnnNo()));
                cell.setCellStyle(headerStyle);
            }

            int stockCol = dynamicStartCol + dynamicColumns.size();
            int weightCol = stockCol + 1;
            Cell stockHeader = mainHeaderRow.createCell(stockCol);
            stockHeader.setCellValue("TON KHO");
            stockHeader.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(headerRowIdx, headerRowIdx + 1, stockCol, stockCol));

            Cell weightHeader = mainHeaderRow.createCell(weightCol);
            weightHeader.setCellValue("KL (KG)");
            weightHeader.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(headerRowIdx, headerRowIdx + 1, weightCol, weightCol));

            List<ShipmentPreviewDTO.ShipmentItem> items = preview.getItems();
            int dataStartRow = headerRowIdx + 2;
            int[] dynamicColTotals = new int[dynamicColumns.size()];
            for (int i = 0; i < items.size(); i++) {
                ShipmentPreviewDTO.ShipmentItem item = items.get(i);
                Row row = sheet.createRow(dataStartRow + i);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(item.getDrawingNumber() != null ? item.getDrawingNumber() : "");
                row.createCell(2).setCellValue(item.getPartName() != null ? item.getPartName() : "");
                row.createCell(3).setCellValue(item.getSpecification() != null ? item.getSpecification() : "");
                row.createCell(4).setCellValue(item.getMaterial() != null ? item.getMaterial() : "");
                row.createCell(5).setCellValue(item.getTotalQty());

                Map<String, Integer> qtyByColKey = new LinkedHashMap<>();
                if (item.getOrderBreakdown() != null) {
                    for (ShipmentPreviewDTO.OrderBreakdown ob : item.getOrderBreakdown()) {
                        String colKey = buildColKey(ob.getVnnNo(), ob.getDeliveryDate());
                        qtyByColKey.put(colKey, qtyByColKey.getOrDefault(colKey, 0) + ob.getQuantity());
                    }
                }

                for (int c = 0; c < dynamicColumns.size(); c++) {
                    int qty = qtyByColKey.getOrDefault(dynamicColumns.get(c).colKey(), 0);
                    if (qty > 0) {
                        row.createCell(dynamicStartCol + c).setCellValue(qty);
                    }
                    dynamicColTotals[c] += qty;
                }

                row.createCell(stockCol).setCellValue("");
                row.createCell(weightCol).setCellValue(item.getWeight() != null ? item.getWeight().doubleValue() : 0);

                for (int c = 0; c <= weightCol; c++) {
                    Cell cell = row.getCell(c);
                    if (cell == null) {
                        cell = row.createCell(c);
                    }
                    if (c == 0 || c == 5 || c >= dynamicStartCol) {
                        cell.setCellStyle(numberCenterStyle);
                    } else {
                        cell.setCellStyle(cellStyle);
                    }
                }
            }

            Row totalRow = sheet.createRow(dataStartRow + items.size());
            Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("Tong");
            totalLabel.setCellStyle(totalStyle);
            sheet.addMergedRegion(new CellRangeAddress(totalRow.getRowNum(), totalRow.getRowNum(), 0, 4));

            Cell totalQtyCell = totalRow.createCell(5);
            totalQtyCell.setCellValue(preview.getTotalQty());
            totalQtyCell.setCellStyle(totalStyle);

            for (int c = 0; c < dynamicColumns.size(); c++) {
                Cell cell = totalRow.createCell(dynamicStartCol + c);
                if (dynamicColTotals[c] > 0) {
                    cell.setCellValue(dynamicColTotals[c]);
                }
                cell.setCellStyle(totalStyle);
            }

            Cell totalStockCell = totalRow.createCell(stockCol);
            totalStockCell.setCellValue("-");
            totalStockCell.setCellStyle(totalStyle);

            double totalWeight = items.stream()
                    .mapToDouble(i -> (i.getWeight() != null ? i.getWeight().doubleValue() : 0) * i.getTotalQty())
                    .sum();
            Cell totalWeightCell = totalRow.createCell(weightCol);
            totalWeightCell.setCellValue(totalWeight);
            totalWeightCell.setCellStyle(totalStyle);

            for (int i = 0; i <= weightCol; i++) {
                sheet.autoSizeColumn(i);
                int currentWidth = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(currentWidth + 600, 15000));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=phieu-xuat-kho-" + preview.getShipmentCode() + ".xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Excel: " + e.getMessage(), e);
        }
    }

    /**
     * Resolve preview: supports both itemIds (new) and orderIds (legacy).
     * itemIds takes priority if both are present.
     */
    @SuppressWarnings("unchecked")
    private ShipmentPreviewDTO resolvePreview(Map<String, Object> body) {
        // Try itemIds first (new partial shipment flow)
        Object itemIdsObj = body.get("itemIds");
        if (itemIdsObj instanceof List<?> rawList && !rawList.isEmpty()) {
            List<Long> itemIds = ((List<Number>) itemIdsObj).stream()
                    .map(Number::longValue)
                    .toList();
            return shipmentService.previewShipmentByItems(itemIds);
        }

        // Fallback to orderIds (legacy flow)
        Object orderIdsObj = body.get("orderIds");
        if (orderIdsObj instanceof List<?> rawList && !rawList.isEmpty()) {
            List<Long> orderIds = ((List<Number>) orderIdsObj).stream()
                    .map(Number::longValue)
                    .toList();
            return shipmentService.previewShipment(orderIds);
        }

        throw new IllegalArgumentException("Either itemIds or orderIds is required");
    }

    private static void setBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private static String safe(String value) {
        return value != null ? value : "";
    }

    private static String buildColKey(String vnnNo, String deliveryDate) {
        return safe(vnnNo) + "|" + safe(deliveryDate);
    }

    private static String formatVnnShort(String vnnNo) {
        if (vnnNo == null || vnnNo.isBlank()) {
            return "-";
        }
        String[] parts = vnnNo.split("-");
        if (parts.length > 1) {
            return parts[0] + "-" + parts[1];
        }
        return vnnNo;
    }

    private static String formatDateForDisplay(String rawDate) {
        if (rawDate == null || rawDate.isBlank()) {
            return "-";
        }
        try {
            LocalDate d = LocalDate.parse(rawDate);
            return d.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (Exception ignored) {
            return rawDate;
        }
    }

    private static List<OrderColumn> buildOrderColumns(ShipmentPreviewDTO preview) {
        Map<String, OrderColumn> map = new LinkedHashMap<>();
        if (preview.getItems() == null) {
            return List.of();
        }

        for (ShipmentPreviewDTO.ShipmentItem item : preview.getItems()) {
            if (item.getOrderBreakdown() == null) {
                continue;
            }
            for (ShipmentPreviewDTO.OrderBreakdown ob : item.getOrderBreakdown()) {
                String key = buildColKey(ob.getVnnNo(), ob.getDeliveryDate());
                map.putIfAbsent(key, new OrderColumn(key, safe(ob.getVnnNo()), safe(ob.getDeliveryDate())));
            }
        }

        return map.values().stream()
                .sorted(Comparator
                        .comparing(OrderColumn::deliveryDate)
                        .thenComparing(OrderColumn::vnnNo))
                .toList();
    }

    private record OrderColumn(String colKey, String vnnNo, String deliveryDate) {
    }
}
