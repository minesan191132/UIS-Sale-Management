package org.example.features.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.JwtTokenProvider;
import org.example.features.auth.dto.AuthResponseDTO;
import org.example.features.auth.dto.ChangePasswordDTO;
import org.example.features.auth.dto.LoginDTO;
import org.example.features.auth.dto.RegisterDTO;
import org.example.features.auth.dto.UpdateProfileDTO;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.example.features.integration.vietqr.dto.CompanyInfoDTO;
import org.example.features.integration.vietqr.service.VietQRService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.example.features.user.entity.UserAddress;
import org.example.features.user.repository.UserAddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Authentication Service
 * Handles registration and login logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final VietQRService vietQRService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redisTemplate;
    private final UserAddressRepository userAddressRepository;

    private static final String REDIS_VERIFY_PREFIX = "verify_token:";
    private static final long VERIFY_TOKEN_EXPIRATION_MINUTES = 3;

    private static final String REDIS_OTP_PREFIX = "otp:";
    private static final String REDIS_RESET_PREFIX = "reset_token:";
    private static final long OTP_EXPIRATION_MINUTES = 5;
    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 10;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Value("${app.backend.url:http://localhost:8080}")
    private String backendBaseUrl;

    /**
     * Register new user with company tax code
     * Fetches company info from VietQR API
     */
    @Transactional
    public String register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
        }

        log.info("Fetching company info for tax code: {}", dto.getTaxCode());
        CompanyInfoDTO companyInfo = vietQRService.getCompanyByTaxCode(dto.getTaxCode());

        Company company = companyRepository.findByTaxCode(dto.getTaxCode())
                .orElseGet(() -> {
                    log.info("Creating new company: {}", companyInfo.getCompanyName());
                    Company newCompany = new Company();
                    newCompany.setCompanyName(companyInfo.getCompanyName());
                    newCompany.setTaxCode(dto.getTaxCode());
                    newCompany.setAddress(companyInfo.getAddress());
                    newCompany.setRepresentative(companyInfo.getRepresentative());
                    newCompany.setPhone(dto.getCompanyPhone());
                    newCompany.setEmail(dto.getCompanyEmail());
                    return companyRepository.save(newCompany);
                });

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        user.setCompany(company);
        user.setRole(UserRole.CUSTOMER);
        user.setIsActive(false);

        User savedUser = userRepository.save(user);

        //TẠO ĐỊA CHỈ MẶC ĐỊNH NGAY SAU KHI TẠO USER
        try {
            String rawAddress = companyInfo.getAddress();
            if (rawAddress != null && !rawAddress.isBlank()) {
                log.info("Creating default address for user {} using company address", savedUser.getEmail());

                // Mặc định dữ liệu nếu không tách được
                String province = "Chưa rõ";
                String district = "Chưa rõ";
                String ward = "Chưa rõ";
                String detail = rawAddress;

                // Thuật toán tách chuỗi địa chỉ theo dấu phẩy (từ dưới lên trên)
                String[] parts = rawAddress.split(",");
                int len = parts.length;

                if (len >= 4) {
                    province = parts[len - 1].trim(); // Phần cuối cùng là Tỉnh/Thành
                    district = parts[len - 2].trim(); // Cấp thứ 2 là Quận/Huyện
                    ward = parts[len - 3].trim();     // Cấp thứ 3 là Phường/Xã

                    // Gom tất cả những phần còn lại ở đầu làm Số nhà/Đường
                    StringBuilder detailBuilder = new StringBuilder();
                    for (int i = 0; i < len - 3; i++) {
                        detailBuilder.append(parts[i].trim());
                        if (i < len - 4) detailBuilder.append(", ");
                    }
                    detail = detailBuilder.toString();
                } else if (len == 3) {
                    // Trường hợp địa chỉ chỉ có 3 phần
                    province = parts[2].trim();
                    district = parts[1].trim();
                    ward = "Chưa rõ";
                    detail = parts[0].trim();
                }

                // Lưu vào Entity
                UserAddress defaultAddress = new UserAddress();
                defaultAddress.setUser(savedUser);
                defaultAddress.setFullName(dto.getFullName());
                defaultAddress.setPhone(dto.getPhone());

                // Đổ dữ liệu đã tách vào đúng 4 cột
                defaultAddress.setProvince(province);
                defaultAddress.setDistrict(district);
                defaultAddress.setWard(ward);
                defaultAddress.setDetail(detail);

                defaultAddress.setIsDefault(true); // Đánh dấu là mặc định

                userAddressRepository.save(defaultAddress);
            }
        } catch (Exception e) {
            log.error("Failed to create default address for user {}: {}", savedUser.getEmail(), e.getMessage());
        }

        log.info("User registered temporarily: {} (company: {})", savedUser.getEmail(), company.getCompanyName());

        String token = UUID.randomUUID().toString();
        String redisKey = REDIS_VERIFY_PREFIX + token;

        redisTemplate.opsForValue().set(
                redisKey,
                savedUser.getEmail(),
                VERIFY_TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES);

        String verificationUrl = buildVerificationUrl(token);

        emailService.sendVerificationEmail(savedUser, verificationUrl);

        return "Đăng ký thành công. Vui lòng kiểm tra email để kích hoạt tài khoản trong vòng 3 phút.";
    }

    /**
     * Verify user token
     */
    @Transactional
    public boolean verifyToken(String token) {
        String redisKey = REDIS_VERIFY_PREFIX + token;

        String email = redisTemplate.opsForValue().get(redisKey);

        if (email == null) {
            log.warn("Invalid or expired verification token received.");
            return false;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        user.setIsActive(true);
        userRepository.save(user);

        redisTemplate.delete(redisKey);

        log.info("Account activated successfully for user: {}", user.getEmail());
        return true;
    }

    /**
     * Resend verification email
     */
    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản nào đăng ký với email này."));

        if (Boolean.TRUE.equals(user.getIsActive())) {
            throw new IllegalArgumentException(
                    "Tài khoản này đã được kích hoạt. Vui lòng chuyển sang trang Đăng nhập.");
        }

        String token = UUID.randomUUID().toString();
        String redisKey = REDIS_VERIFY_PREFIX + token;

        redisTemplate.opsForValue().set(
                redisKey,
                user.getEmail(),
                VERIFY_TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES);

        String verificationUrl = buildVerificationUrl(token);
        emailService.sendVerificationEmail(user, verificationUrl);

        return "Email kích hoạt đã được gửi lại thành công. Vui lòng kiểm tra hộp thư (và thư rác) của bạn.";
    }

    private String buildVerificationUrl(String token) {
        String normalizedBaseUrl = normalizeBaseUrl(backendBaseUrl);
        return normalizedBaseUrl + "/api/auth/verify/" + token;
    }

    private String normalizeBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return "http://localhost:8080";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    /**
     * Login with email and password
     */
    @Transactional
    public AuthResponseDTO login(LoginDTO dto) {
        // 1. Tìm user trước để kiểm tra trạng thái
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email hoặc mật khẩu không chính xác"));

        // 2. Kiểm tra trạng thái tài khoản
        if (Boolean.FALSE.equals(user.getIsActive())) {
            // Nếu đã từng đăng nhập → bị admin khoá; ngược lại chưa kích hoạt email
            if (user.getLastLogin() != null) {
                throw new DisabledException(
                        "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ.");
            } else {
                throw new DisabledException(
                        "Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email để xác thực hoặc yêu cầu gửi lại email.");
            }
        }

        // 3. Xác thực người dùng (Kiểm tra mật khẩu)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()));

        // 4. Tạo JWT token
        String token = jwtTokenProvider.generateToken(user);

        // 5. Cập nhật thời gian đăng nhập cuối
        user.setLastLogin(java.time.LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getEmail());

        // 6. Trả về response
        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .companyId(user.getCompany() != null ? user.getCompany().getId() : null)
                .companyName(user.getCompany() != null ? user.getCompany().getCompanyName() : null)
                .role(user.getRole().name())
                .expiresIn(jwtExpiration)
                .build();
    }

    /**
     * Get full user profile from database
     */
    public Map<String, Object> getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getId());
        profile.put("email", user.getEmail());
        profile.put("fullName", user.getFullName());
        profile.put("companyId", user.getCompany() != null ? user.getCompany().getId() : null);
        profile.put("role", user.getRole().name());
        profile.put("phone", user.getPhone());
        profile.put("gender", user.getGender());
        profile.put("dobDay", user.getDobDay());
        profile.put("dobMonth", user.getDobMonth());
        profile.put("dobYear", user.getDobYear());
        // Company info
        if (user.getCompany() != null) {
            profile.put("taxCode", user.getCompany().getTaxCode());
            profile.put("companyName", user.getCompany().getCompanyName());
            profile.put("companyAddress", user.getCompany().getAddress());
            profile.put("companyRepresentative", user.getCompany().getRepresentative());
            profile.put("companyPhone", user.getCompany().getPhone());
            profile.put("companyEmail", user.getCompany().getEmail());
        }
        return profile;
    }

    /**
     * Update user profile
     */
    @Transactional
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        if (dto.getFullName() != null)
            user.setFullName(dto.getFullName());
        if (dto.getPhone() != null)
            user.setPhone(dto.getPhone());
        if (dto.getGender() != null)
            user.setGender(dto.getGender());
        if (dto.getDobDay() != null)
            user.setDobDay(dto.getDobDay());
        if (dto.getDobMonth() != null)
            user.setDobMonth(dto.getDobMonth());
        if (dto.getDobYear() != null)
            user.setDobYear(dto.getDobYear());

        userRepository.save(user);
        log.info("Profile updated for user: {}", user.getEmail());
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu hiện tại không chính xác");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getEmail());
    }

    /**
     * Step 1: Send OTP to email for password reset
     */
    public String forgotPassword(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email không hợp lệ hoặc chưa được đăng ký."));

        String otp = String.format("%06d", new SecureRandom().nextInt(1_000_000));
        String redisKey = REDIS_OTP_PREFIX + email;

        redisTemplate.opsForValue().set(redisKey, otp, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        emailService.sendOtpEmail(email, otp);

        log.info("OTP sent to: {}", email);
        return "Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra hộp thư trong vòng 5 phút.";
    }

    /**
     * Step 2: Verify OTP → return one-time reset token
     */
    public String verifyOtp(String email, String otp) {
        String redisKey = REDIS_OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            throw new IllegalArgumentException("OTP không hợp lệ hoặc đã hết hạn.");
        }

        // Xóa OTP ngay sau khi xác thực thành công (one-time use)
        redisTemplate.delete(redisKey);

        String resetToken = UUID.randomUUID().toString();
        String resetKey = REDIS_RESET_PREFIX + resetToken;
        redisTemplate.opsForValue().set(resetKey, email, RESET_TOKEN_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        log.info("OTP verified, reset token issued for: {}", email);
        return resetToken;
    }

    /**
     * Step 3: Reset password using one-time reset token
     */
    @Transactional
    public String resetPassword(String resetToken, String newPassword) {
        String resetKey = REDIS_RESET_PREFIX + resetToken;
        String email = redisTemplate.opsForValue().get(resetKey);

        if (email == null) {
            throw new IllegalArgumentException("Token không hợp lệ hoặc đã hết hạn. Vui lòng thực hiện lại từ đầu.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại."));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa reset token để không thể dùng lại
        redisTemplate.delete(resetKey);

        log.info("Password reset successfully for: {}", email);
        return "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập lại.";
    }
}