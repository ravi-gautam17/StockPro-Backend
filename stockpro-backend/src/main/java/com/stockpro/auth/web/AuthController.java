package com.stockpro.auth.web;

import com.stockpro.auth.dto.AuthResponse;
import com.stockpro.auth.dto.LoginRequest;
import com.stockpro.auth.dto.RegisterRequest;
import com.stockpro.auth.dto.UserResponse;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication REST boundary. React posts credentials here; receives JWT for subsequent API calls.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Login (no JWT required)", security = {})
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.email(), req.password());
        var user = userRepository.findByEmail(req.email()).orElseThrow();
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }

    /** Open registration for demo; in production restrict to Admin-only user provisioning. */
    @Operation(summary = "Self-register (no JWT required)", security = {})
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest req) {
        var u = authService.register(req.fullName(), req.email(), req.password(), req.role(), req.department());
        return UserResponse.from(u);
    }

    @Operation(summary = "Current user profile")
    @SecurityRequirement(name = "bearer")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            return ResponseEntity.status(401).build();
        }
        return userRepository.findByEmail(auth.getName())
                .map(u -> ResponseEntity.ok(UserResponse.from(u)))
                .orElse(ResponseEntity.status(401).build());
    }
}
