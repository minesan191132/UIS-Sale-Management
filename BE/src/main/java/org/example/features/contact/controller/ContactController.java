package org.example.features.contact.controller;

import lombok.RequiredArgsConstructor;
import org.example.features.contact.dto.ContactRequestDTO;
import org.example.features.contact.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public endpoint — không yêu cầu đăng nhập
 * POST /api/contact/send
 */
@RestController
@RequestMapping("/api/contact")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/send")
    public ResponseEntity<?> sendContact(@RequestBody ContactRequestDTO dto) {
        if (dto.getFullName() == null || dto.getFullName().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập họ và tên."));
        }
        if (dto.getPhone() == null || dto.getPhone().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập số điện thoại."));
        }
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Vui lòng nhập nội dung tin nhắn."));
        }

        contactService.sendContactEmail(dto);
        return ResponseEntity.ok(Map.of("message", "Yêu cầu của bạn đã được gửi thành công!"));
    }
}
