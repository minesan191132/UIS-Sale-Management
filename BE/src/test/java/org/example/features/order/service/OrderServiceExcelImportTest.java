package org.example.features.order.service;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.example.features.order.dto.OrderItemDTO;
import org.example.features.order.repository.OrderItemRepository;
import org.example.features.order.repository.OrderRepository;
import org.example.features.payment.service.PaymentService;
import org.example.features.productadmin.AdminProductService;
import org.example.features.warehouse.repository.DrawingMetaRepository;
import org.example.features.warehouse.service.QuotePricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class OrderServiceExcelImportTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                mock(OrderRepository.class),
                mock(OrderItemRepository.class),
                mock(UserRepository.class),
                mock(CompanyRepository.class),
                mock(PaymentService.class),
                mock(QuotePricingService.class),
                mock(DrawingMetaRepository.class),
                mock(AdminProductService.class));
    }

    @Test
    void parseExcelFile_shouldMapByHeader_whenColumnsAreReordered() throws Exception {
        String[] headers = {
                "Quantity", "Part Name", "VNN_NO", "Delivery Date", "Material", "Specification", "Item Code", "Drawing Number"
        };

        String[] rowValues = {
                "12", "Bolt M12", "VNN-2026-001", "2026/03/27", "SUS304", "M12x50", "ITM-001", "DR-001"
        };

        MultipartFile file = createExcelFile("梱包指示", headers, rowValues);
        List<OrderItemDTO> result = invokeParseExcelFile(file);

        assertEquals(1, result.size());
        OrderItemDTO item = result.get(0);
        assertEquals("VNN-2026-001", item.getUnit());
        assertEquals("ITM-001", item.getItemCode());
        assertEquals("DR-001", item.getDrawingNumber());
        assertEquals("Bolt M12", item.getItemName());
        assertEquals("M12x50", item.getSpecification());
        assertEquals("SUS304", item.getMaterial());
        assertEquals(12, item.getQuantity());
        assertEquals("2026-03-27", item.getDeliveryDate());
    }

    @Test
    void parseExcelFile_shouldAcceptJapaneseAliases() throws Exception {
        String[] headers = {
                "受注番号", "品目コード", "図番", "品名", "型式", "材質", "数量", "希望納期"
        };

        String[] rowValues = {
                "VNN-2026-002", "ITM-002", "DR-002", "Plate A", "A36", "SS400", "8", "2026年03月18日"
        };

        MultipartFile file = createExcelFile("Dg", headers, rowValues);
        List<OrderItemDTO> result = invokeParseExcelFile(file);

        assertEquals(1, result.size());
        OrderItemDTO item = result.get(0);
        assertEquals("VNN-2026-002", item.getUnit());
        assertEquals("ITM-002", item.getItemCode());
        assertEquals("DR-002", item.getDrawingNumber());
        assertEquals("Plate A", item.getItemName());
        assertEquals("A36", item.getSpecification());
        assertEquals("SS400", item.getMaterial());
        assertEquals(8, item.getQuantity());
        assertEquals("2026-03-18", item.getDeliveryDate());
    }

    @Test
    void parseExcelFile_shouldThrow_whenRequiredHeaderMissing() throws Exception {
        String[] headers = {
                "VNN_NO", "Item Code", "Drawing Number", "Part Name", "Specification", "Quantity", "Delivery Date"
        };

        String[] rowValues = {
                "VNN-2026-003", "ITM-003", "DR-003", "Shaft", "S45C", "5", "2026-03-30"
        };

        MultipartFile file = createExcelFile("梱包指示", headers, rowValues);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> invokeParseExcelFile(file));
        assertTrue(ex.getMessage().contains("Missing required headers:"));
        assertTrue(ex.getMessage().contains("Material"));
    }

    @Test
    void parseExcelFile_shouldSkipRow_whenRequiredCellIsBlank() throws Exception {
        String[] headers = {
                "VNN_NO", "Item Code", "Drawing Number", "Part Name", "Specification", "Material", "Quantity", "Delivery Date"
        };

        String[] rowValues = {
                "VNN-2026-004", "", "DR-004", "Bracket", "BKT", "SUS201", "15", "2026-04-01"
        };

        MultipartFile file = createExcelFile("梱包指示", headers, rowValues);
        List<OrderItemDTO> result = invokeParseExcelFile(file);

        assertTrue(result.isEmpty());
    }

    @Test
    void parseExcelFile_shouldParseDeliveryDate_whenExcelDateCellIsNumeric() throws Exception {
        String[] headers = {
                "VNN_NO", "Item Code", "Drawing Number", "Part Name", "Specification", "Material", "Quantity", "Delivery Date"
        };

        String[] rowValues = {
                "VNN-2026-005", "ITM-005", "DR-005", "Housing", "HSG-01", "S45C", "6", ""
        };

        MultipartFile file = createExcelFileWithNumericDate(
                "梱包指示",
                headers,
                rowValues,
                7,
                LocalDate.of(2026, 4, 15));

        List<OrderItemDTO> result = invokeParseExcelFile(file);

        assertEquals(1, result.size());
        assertEquals("2026-04-15", result.get(0).getDeliveryDate());
    }

    @Test
    void parseExcelFile_shouldParseDeliveryDate_whenFormatIsDayMonthYear() throws Exception {
        String[] headers = {
                "VNN_NO", "Item Code", "Drawing Number", "Part Name", "Specification", "Material", "Quantity", "Delivery Date"
        };

        String[] rowValues = {
                "VNN-2026-006", "ITM-006", "DR-006", "Base", "BASE-01", "SUS304", "10", "27/11/2026"
        };

        MultipartFile file = createExcelFile("梱包指示", headers, rowValues);
        List<OrderItemDTO> result = invokeParseExcelFile(file);

        assertEquals(1, result.size());
        assertEquals("2026-11-27", result.get(0).getDeliveryDate());
    }

    @Test
    void parseExcelFile_shouldParseDeliveryDate_whenFormatIsMonthDayYear() throws Exception {
        String[] headers = {
                "VNN_NO", "Item Code", "Drawing Number", "Part Name", "Specification", "Material", "Quantity", "Delivery Date"
        };

        String[] rowValues = {
                "VNN-2026-007", "ITM-007", "DR-007", "Cover", "CVR-01", "S45C", "4", "11/27/2026"
        };

        MultipartFile file = createExcelFile("梱包指示", headers, rowValues);
        List<OrderItemDTO> result = invokeParseExcelFile(file);

        assertEquals(1, result.size());
        assertEquals("2026-11-27", result.get(0).getDeliveryDate());
    }

    private List<OrderItemDTO> invokeParseExcelFile(MultipartFile file) throws Exception {
        Method method = OrderService.class.getDeclaredMethod("parseExcelFile", MultipartFile.class);
        method.setAccessible(true);

        try {
            @SuppressWarnings("unchecked")
            List<OrderItemDTO> result = (List<OrderItemDTO>) method.invoke(orderService, file);
            return result;
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            if (cause instanceof Exception checkedException) {
                throw checkedException;
            }
            throw new RuntimeException(cause);
        }
    }

    private MultipartFile createExcelFile(String sheetName, String[] headers, String[] rowValues) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            Row dataRow = sheet.createRow(1);
            for (int i = 0; i < rowValues.length; i++) {
                dataRow.createCell(i).setCellValue(rowValues[i]);
            }

            workbook.write(output);
            byte[] bytes = output.toByteArray();
            return new MockMultipartFile(
                    "file",
                    "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    bytes.length == 0 ? "".getBytes(StandardCharsets.UTF_8) : bytes);
        }
    }

    private MultipartFile createExcelFileWithNumericDate(
            String sheetName,
            String[] headers,
            String[] rowValues,
            int dateColumnIndex,
            LocalDate dateValue) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            Row dataRow = sheet.createRow(1);
            for (int i = 0; i < rowValues.length; i++) {
                dataRow.createCell(i).setCellValue(rowValues[i]);
            }

            var dateCell = dataRow.getCell(dateColumnIndex);
            if (dateCell == null) {
                dateCell = dataRow.createCell(dateColumnIndex);
            }

            var style = workbook.createCellStyle();
            short dateFormat = workbook.createDataFormat().getFormat("yyyy-mm-dd");
            style.setDataFormat(dateFormat);
            dateCell.setCellStyle(style);
            dateCell.setCellValue(java.util.Date.from(dateValue.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            workbook.write(output);
            return new MockMultipartFile(
                    "file",
                    "test.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    output.toByteArray());
        }
    }
}
