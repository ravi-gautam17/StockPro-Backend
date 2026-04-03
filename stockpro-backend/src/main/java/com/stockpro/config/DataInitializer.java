package com.stockpro.config;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;
import com.stockpro.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Ensures a bootstrap admin exists for first login (dev/demo). Password: {@code Admin@123}
 * Disable or replace with proper onboarding in production.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (users.existsByEmail("admin@stockpro.local")) {
                return;
            }
            User admin = new User();
            admin.setFullName("System Admin");
            admin.setEmail("admin@stockpro.local");
            admin.setPasswordHash(encoder.encode("Admin@123"));
            admin.setRole(UserRole.ADMIN);
            admin.setDepartment("IT");
            admin.setActive(true);
            users.save(admin);
            log.info("Seeded default admin: admin@stockpro.local / Admin@123");
        };
    }
}