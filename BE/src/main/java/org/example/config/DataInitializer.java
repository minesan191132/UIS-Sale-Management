package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.features.company.entity.Company;
import org.example.features.company.entity.User;
import org.example.features.company.entity.UserRole;
import org.example.features.company.repository.CompanyRepository;
import org.example.features.company.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds sample users into the database on startup (non-production).
 * Safe to run multiple times — uses existsByEmail guard.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!prod") // skip in production
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        Company company = ensureCompany();
        seedUsers(company);
    }

    private Company ensureCompany() {
        return companyRepository.findByTaxCode("0000000001")
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setCompanyName("Công ty Demo");
                    c.setTaxCode("0000000001");
                    c.setAddress("Hà Nội, Việt Nam");
                    c.setPhone("0900000001");
                    c.setEmail("demo@company.vn");
                    return companyRepository.save(c);
                });
    }

    private void seedUsers(Company company) {
        List<SeedUser> seeds = List.of(
                new SeedUser("phuc1@gmail.com", "Nguyễn Văn Phúc", "phuc2313", UserRole.ADMIN),
                new SeedUser("admin@gmail.com", "Quản trị viên", "admin123", UserRole.ADMIN),
                new SeedUser("user01@gmail.com", "Trần Văn Nam", "user1234", UserRole.CUSTOMER),
                new SeedUser("user02@gmail.com", "Lê Thị Hoa", "user1234", UserRole.CUSTOMER),
                new SeedUser("user03@gmail.com", "Phạm Minh Tuấn", "user1234", UserRole.CUSTOMER));

        int created = 0;
        for (SeedUser s : seeds) {
            if (!userRepository.existsByEmail(s.email)) {
                User user = new User();
                user.setUsername(s.email);
                user.setEmail(s.email);
                user.setFullName(s.fullName);
                user.setPassword(passwordEncoder.encode(s.password));
                user.setRole(s.role);
                user.setIsActive(true);
                user.setCompany(company);
                userRepository.save(user);
                created++;
                log.info("✅ Seeded user: {} ({})", s.email, s.role);
            }
        }

        if (created > 0) {
            log.info("🌱 DataInitializer: {} sample user(s) created.", created);
        } else {
            log.info("🌱 DataInitializer: all sample users already exist, skipping.");
        }
    }

    private record SeedUser(String email, String fullName, String password, UserRole role) {
    }
}
