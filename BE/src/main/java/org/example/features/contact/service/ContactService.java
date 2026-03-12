package org.example.features.contact.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.contact.dto.ContactRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    // ✅ Email nhận yêu cầu liên hệ — chỉnh tại application.properties
    @Value("${contact.receiver.email}")
    private String receiverEmail;

    @Async
    public void sendContactEmail(ContactRequestDTO dto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(receiverEmail);
        message.setReplyTo(dto.getEmail() != null ? dto.getEmail() : from);
        message.setSubject("[Liên Hệ UIS] " + (dto.getSubject() != null ? dto.getSubject() : "Yêu cầu mới"));
        message.setText(buildEmailBody(dto));
        mailSender.send(message);
        log.info("Contact email sent to {} from {}", receiverEmail, dto.getEmail());
    }

    private String buildEmailBody(ContactRequestDTO dto) {
        return """
                Bạn có một yêu cầu liên hệ mới từ website UIS.
                
                ─────────────────────────────
                Họ và tên  : %s
                Số điện thoại: %s
                Email      : %s
                Chủ đề     : %s
                ─────────────────────────────
                Nội dung:
                %s
                ─────────────────────────────
                
                (Email này được gửi tự động từ form liên hệ website UIS)
                """.formatted(
                nvl(dto.getFullName()),
                nvl(dto.getPhone()),
                nvl(dto.getEmail()),
                nvl(dto.getSubject()),
                nvl(dto.getMessage())
        );
    }

    private String nvl(String value) {
        return value != null ? value : "(không có)";
    }
}
