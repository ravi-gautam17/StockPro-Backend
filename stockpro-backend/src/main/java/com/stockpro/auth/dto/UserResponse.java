package com.stockpro.auth.dto;

import com.stockpro.auth.domain.User;
import com.stockpro.auth.domain.UserRole;

public record UserResponse(Long id, String fullName, String email, String phone,
                           UserRole role, String department, boolean active) {

    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(), u.getFullName(), u.getEmail(), u.getPhone(),
                u.getRole(), u.getDepartment(), u.isActive());
    }
}