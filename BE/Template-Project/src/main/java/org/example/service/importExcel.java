package org.example.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class importExcel {

    public void importOrderItems(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            for (Row row : sheet) {
                // 1. Bỏ qua dòng tiêu đề (Dòng đầu tiên thường là index 0)
                if (row.getRowNum() == 0) continue;

                // 2. Kiểm tra dòng trống để tránh lỗi NullPointer
                if (isRowEmpty(row)) continue;

                // 3. Lấy dữ liệu từ cột B (index 1) đến cột I (index 8)
                String colB = getCellValue(row.getCell(1), evaluator);
                String colC = getCellValue(row.getCell(2), evaluator);
                String colD = getCellValue(row.getCell(3), evaluator);
                String colE = getCellValue(row.getCell(4), evaluator);
                String colF = getCellValue(row.getCell(5), evaluator);
                String colG = getCellValue(row.getCell(6), evaluator);

                // Giả sử cột H và I là số (Số lượng, Đơn giá...)
                int colH = getInt(row.getCell(7));
                int colI = getInt(row.getCell(8));

                // 4. Gọi Repository hoặc dùng JDBC để lưu vào PostgreSQL
                // ví dụ: orderRepository.save(new OrderItem(colB, colC, ..., colI));
                System.out.println("Đang đọc dòng " + row.getRowNum() + ": " + colB + " | " + colI);
            }
        } catch (Exception e) {
            throw new RuntimeException("Import Excel failed: " + e.getMessage());
        }
    }

    // Hàm đọc String an toàn từ mọi kiểu ô
    private String getCellValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";

        // Nếu ô chứa công thức, ta cần "tính toán" nó trước
        if (cell.getCellType() == CellType.FORMULA) {
            CellValue cellValue = evaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case NUMERIC: return String.valueOf((int) cellValue.getNumberValue());
                case STRING: return cellValue.getStringValue();
                default: return "";
            }
        }

        // Đối với các ô không phải công thức (số, chữ bình thường)
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    // Hàm đọc Integer an toàn (đã sửa lỗi IllegalStateException trước đó)
    private int getInt(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) { return 0; }
        }
        return 0;
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) return false;
        }
        return true;
    }
}