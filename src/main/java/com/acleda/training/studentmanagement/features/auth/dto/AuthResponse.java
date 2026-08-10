package com.acleda.training.studentmanagement.features.auth.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Long expiresIn,
        Long refreshExpiresIn,
        String username,
        List<String> roles
) {
}