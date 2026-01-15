package org.example.controller;

import org.example.service.importExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

@RestController
@RequestMapping("/api/orders")
public class OrderImportController {

  @Autowired
  importExcel service;

    public OrderImportController(importExcel service) {
        this.service = service;
    }

    @PostMapping("/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) { // ĐÚNG
        try {
            // Lấy InputStream từ MultipartFile
            InputStream inputStream = file.getInputStream();

            // Gọi Service xử lý
            service.importOrderItems(inputStream);

            return ResponseEntity.ok("Import thành công");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }
}

