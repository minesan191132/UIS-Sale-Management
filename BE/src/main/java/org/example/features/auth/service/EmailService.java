package org.example.features.auth.service;

import org.example.features.company.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final HttpServletRequest request;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(toEmail);
        message.setSubject("Mã xác nhận đặt lại mật khẩu");
        message.setText("""
                Xin chào,
                
                Mã OTP của bạn là: %s
                
                Mã này chỉ có hiệu lực trong thời gian phiên làm việc hiện tại.
                
                Trân trọng,
                Hệ thống hỗ trợ tài khoản UIS.
                """.formatted(otp));
        mailSender.send(message);
    }

    @Async
    public void sendVerificationEmail(User user, String verificationUrl) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(user.getEmail());
        email.setSubject("Kích hoạt tài khoản UIS của bạn");
        email.setText("Xin chào " + user.getUsername() + ",\n\n"
                + "Cảm ơn bạn đã đăng ký. Vui lòng nhấp vào liên kết bên dưới để kích hoạt tài khoản của bạn:\n"
                + verificationUrl + "\n\n"
                + "Liên kết này sẽ hết hạn sau 3 phút.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ UIS");

        mailSender.send(email);
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Async
    public void sendDelayNotification(User user, String orderNumber, LocalDate newDeliveryDate, String reason) {
        String formattedDate = newDeliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(user.getEmail());
        email.setSubject("[UIS] Thông báo cập nhật ngày giao hàng - Đơn " + orderNumber);
        email.setText("Kính gửi " + user.getFullName() + ",\n\n"
                + "Chúng tôi xin thông báo rằng đơn hàng " + orderNumber + " của bạn đã được cập nhật ngày giao.\n\n"
                + "Ngày giao mới: " + formattedDate + "\n"
                + "Lý do: " + reason + "\n\n"
                + "Chúng tôi thành thật xin lỗi vì sự bất tiện này và cam kết hoàn thành đơn hàng đúng hạn mới.\n\n"
                + "Nếu có thắc mắc, vui lòng liên hệ với chúng tôi.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ UIS");
        mailSender.send(email);
        log.info("Delay notification sent to {} for order {}", user.getEmail(), orderNumber);
    }

    @Async
    public void sendRemainingPaymentReminder(User user, String orderNumber,
                                             BigDecimal remainingAmount, LocalDate deliveryDate) {
        String formattedDate = deliveryDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String formattedAmount = String.format("%,.0f VNĐ", remainingAmount);
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom(from);
        email.setTo(user.getEmail());
        email.setSubject("[UIS] Nhắc nhở thanh toán đợt 2 - Đơn " + orderNumber);
        email.setText("Kính gửi " + user.getFullName() + ",\n\n"
                + "Đơn hàng gia công " + orderNumber + " của bạn sắp hoàn thành.\n\n"
                + "Số tiền cần thanh toán (30% còn lại): " + formattedAmount + "\n"
                + "Ngày giao dự kiến: " + formattedDate + "\n\n"
                + "Vui lòng thanh toán phần còn lại trước ngày giao hàng để đảm bảo quy trình giao nhận diễn ra suôn sẻ.\n\n"
                + "Bạn có thể thanh toán trực tiếp qua hệ thống tại trang Lịch sử đơn hàng.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ UIS");
        mailSender.send(email);
        log.info("Remaining payment reminder sent to {} for order {}", user.getEmail(), orderNumber);
    }

}

