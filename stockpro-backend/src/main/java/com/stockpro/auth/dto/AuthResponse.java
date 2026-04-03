package com.stockpro.auth.dto;

import com.stockpro.auth.domain.UserRole;

public record AuthResponse(String token, Long userId, String email, String fullName, UserRole role) {
}
