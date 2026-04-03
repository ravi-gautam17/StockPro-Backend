package com.stockpro.auth.service;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.config.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Validates credentials via Spring Security's {@link AuthenticationManager}, then issues JWT.
     */
    @Transactional
    public String login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setLastLoginAt(Instant.now());
        return jwtService.generateToken(user);
    }

    @Transactional
    public User register(String fullName, String email, String password, UserRole role, String department) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole(role != null ? role : UserRole.STAFF);
        u.setDepartment(department);
        u.setActive(true);
        return userRepository.save(u);
    }
}
