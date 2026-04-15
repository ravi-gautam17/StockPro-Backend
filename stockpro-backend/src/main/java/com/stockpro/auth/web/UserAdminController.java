package com.stockpro.auth.web;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;
import com.stockpro.auth.dto.UserResponse;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.auth.service.AuthService;
import com.stockpro.audit.service.AuditService;
import com.stockpro.auth.service.CurrentUserService;
import com.stockpro.warehouse.service.WarehouseAssignmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin user provisioning — case study: internal accounts created by Administrator.
 */
@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearer")
public class UserAdminController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AuditService auditService;
    private final CurrentUserService currentUserService;
    private final WarehouseAssignmentService warehouseAssignmentService;

    public UserAdminController(UserRepository userRepository,
                               AuthService authService,
                               AuditService auditService,
                               CurrentUserService currentUserService,
                               WarehouseAssignmentService warehouseAssignmentService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.auditService = auditService;
        this.currentUserService = currentUserService;
        this.warehouseAssignmentService = warehouseAssignmentService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> list() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse create(@RequestBody AdminCreateUserRequest body) {
        var u = authService.register(body.fullName(), body.email(), body.password(), body.role(), body.department());
        auditService.log(currentUserService.requireCurrentUser().getId(), "USER_CREATE", "User",
                String.valueOf(u.getId()), body.email());
        return UserResponse.from(u);
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deactivate(@PathVariable Long id) {
        User u = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        u.setActive(false);
        auditService.log(currentUserService.requireCurrentUser().getId(), "USER_DEACTIVATE", "User",
                String.valueOf(id), null);
    }

    @GetMapping("/{id}/warehouse-assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Long> getWarehouseAssignments(@PathVariable Long id) {
        return warehouseAssignmentService.getWarehouseIdsForUser(id);
    }

    @PutMapping("/{id}/warehouse-assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public void replaceWarehouseAssignments(@PathVariable Long id, @RequestBody List<Long> warehouseIds) {
        warehouseAssignmentService.replaceAssignments(id, warehouseIds != null ? warehouseIds : List.of(),
                currentUserService.requireCurrentUser().getId());
    }

    public record AdminCreateUserRequest(
            @NotBlank String fullName,
            @NotBlank String email,
            @NotBlank String password,
            UserRole role,
            String department) {}
}
