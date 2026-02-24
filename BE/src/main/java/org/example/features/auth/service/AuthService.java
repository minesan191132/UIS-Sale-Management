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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        private final AuthenticationManager authenticationManager;

        @Value("${jwt.expiration:86400000}")
        private long jwtExpiration;

        /**
         * Register new user with company tax code
         * Fetches company info from VietQR API
         */
        @Transactional
        public AuthResponseDTO register(RegisterDTO dto) {
                // 1. Check if email already exists
                if (userRepository.existsByEmail(dto.getEmail())) {
                        throw new IllegalArgumentException("Email already registered: " + dto.getEmail());
                }

                // 2. Fetch company info from VietQR
                log.info("Fetching company info for tax code: {}", dto.getTaxCode());
                CompanyInfoDTO companyInfo = vietQRService.getCompanyByTaxCode(dto.getTaxCode());

                // 3. Check if company already exists (by tax code)
                Company company = companyRepository.findByTaxCode(dto.getTaxCode())
                                .orElseGet(() -> {
                                        // Create new company
                                        log.info("Creating new company: {}", companyInfo.getCompanyName());
                                        Company newCompany = new Company();

                                        // Data from VietQR
                                        newCompany.setCompanyName(companyInfo.getCompanyName());
                                        newCompany.setTaxCode(dto.getTaxCode()); // Use tax code from user input
                                        newCompany.setAddress(companyInfo.getAddress());
                                        newCompany.setRepresentative(companyInfo.getRepresentative());

                                        // Contact info from user input (not from VietQR)
                                        newCompany.setPhone(dto.getCompanyPhone()); // Required field from user
                                        newCompany.setEmail(dto.getCompanyEmail()); // Optional field from user (can be
                                                                                    // null)

                                        return companyRepository.save(newCompany);
                                });

                // 4. Create user
                User user = new User();
                user.setEmail(dto.getEmail());
                user.setUsername(dto.getEmail()); // Use email as username
                user.setPassword(passwordEncoder.encode(dto.getPassword()));
                user.setFullName(dto.getFullName());
                user.setCompany(company);
                user.setRole(UserRole.CUSTOMER);
                user.setIsActive(true);

                User savedUser = userRepository.save(user);

                log.info("User registered successfully: {} (company: {})",
                                savedUser.getEmail(), company.getCompanyName());

                // 5. Generate JWT token
                String token = jwtTokenProvider.generateToken(savedUser);

                // 6. Return response
                return AuthResponseDTO.builder()
                                .token(token)
                                .tokenType("Bearer")
                                .userId(savedUser.getId())
                                .email(savedUser.getEmail())
                                .fullName(savedUser.getFullName())
                                .companyId(company.getId())
                                .companyName(company.getCompanyName())
                                .role(savedUser.getRole().name())
                                .expiresIn(jwtExpiration)
                                .build();
        }

        /**
         * Login with email and password
         */
        public AuthResponseDTO login(LoginDTO dto) {
                // 1. Authenticate user
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                dto.getEmail(),
                                                dto.getPassword()));

                // 2. Find user
                User user = userRepository.findByEmail(dto.getEmail())
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // 3. Generate JWT token
                String token = jwtTokenProvider.generateToken(user);

                // 4. Update last login
                user.setLastLogin(java.time.LocalDateTime.now());
                userRepository.save(user);

                log.info("User logged in: {}", user.getEmail());

                // 5. Return response
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
