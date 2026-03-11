package org.example.features.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.config.security.JwtTokenProvider;
import org.example.features.auth.dto.AuthResponseDTO;
import org.example.features.auth.dto.LoginDTO;
import org.example.features.auth.dto.RegisterDTO;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final String REDIS_VERIFY_PREFIX = "verify_token:";
    private static final long VERIFY_TOKEN_EXPIRATION_MINUTES = 3;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

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
        user.setCompany(company);
        user.setRole(UserRole.CUSTOMER);
        user.setIsActive(false);

        User savedUser = userRepository.save(user);
        log.info("User registered temporarily: {} (company: {})", savedUser.getEmail(), company.getCompanyName());

        String token = UUID.randomUUID().toString();
        String redisKey = REDIS_VERIFY_PREFIX + token;

        redisTemplate.opsForValue().set(
                redisKey,
                savedUser.getEmail(),
                VERIFY_TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES);

        String verificationUrl = "http://localhost:8080/api/auth/verify/" + token;

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

        String verificationUrl = "http://localhost:8080/api/auth/verify/" + token;
        emailService.sendVerificationEmail(user, verificationUrl);

        return "Email kích hoạt đã được gửi lại thành công. Vui lòng kiểm tra hộp thư (và thư rác) của bạn.";
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
}