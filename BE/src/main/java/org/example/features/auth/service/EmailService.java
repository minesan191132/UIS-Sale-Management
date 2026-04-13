package org.example.features.auth.service;

import org.example.features.company.entity.User;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String from;

    // ==================== OTP EMAIL ====================

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            Context ctx = new Context();
            ctx.setVariable("otp", otp);

            String html = templateEngine.process("email/otp-email", ctx);
            sendHtmlEmail(toEmail, "[UIS] Mã xác nhận đặt lại mật khẩu", html);

            log.info("OTP email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
        }
    }

    // ==================== VERIFICATION EMAIL ====================

    @Async
    public void sendVerificationEmail(User user, String verificationUrl) {
        try {
            Context ctx = new Context();
            ctx.setVariable("username", user.getFullName() != null ? user.getFullName() : user.getUsername());
            ctx.setVariable("verificationUrl", verificationUrl);

            String html = templateEngine.process("email/verification-email", ctx);
            sendHtmlEmail(user.getEmail(), "[UIS] Kích hoạt tài khoản của bạn", html);

            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // ==================== DELAY NOTIFICATION ====================

    @Async
    public void sendDelayNotification(User user, String orderNumber, LocalDate newDeliveryDate, String reason) {
        try {
            Context ctx = new Context();
            ctx.setVariable("fullName", user.getFullName());
            ctx.setVariable("orderNumber", orderNumber);
            ctx.setVariable("newDeliveryDate", newDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            ctx.setVariable("reason", reason);

            String html = templateEngine.process("email/delay-notification", ctx);
            sendHtmlEmail(user.getEmail(), "[UIS] Thông báo cập nhật ngày giao hàng - Đơn " + orderNumber, html);

            log.info("Delay notification sent to {} for order {}", user.getEmail(), orderNumber);
        } catch (Exception e) {
            log.error("Failed to send delay notification to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // ==================== REMAINING PAYMENT REMINDER ====================

    @Async
    public void sendRemainingPaymentReminder(User user, String orderNumber,
                                             BigDecimal remainingAmount, LocalDate deliveryDate) {
        try {
            Context ctx = new Context();
            ctx.setVariable("fullName", user.getFullName());
            ctx.setVariable("orderNumber", orderNumber);
            ctx.setVariable("remainingAmount", String.format("%,.0f VNĐ", remainingAmount));
            ctx.setVariable("deliveryDate", deliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            String html = templateEngine.process("email/payment-reminder", ctx);
            sendHtmlEmail(user.getEmail(), "[UIS] Nhắc nhở thanh toán đợt 2 - Đơn " + orderNumber, html);

            log.info("Remaining payment reminder sent to {} for order {}", user.getEmail(), orderNumber);
        } catch (Exception e) {
            log.error("Failed to send payment reminder to {}: {}", user.getEmail(), e.getMessage());
        }
    }

    // ==================== SHARED HELPER ====================

    private void sendHtmlEmail(String to, String subject, String html) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true = nội dung HTML
        mailSender.send(message);
    }
}
