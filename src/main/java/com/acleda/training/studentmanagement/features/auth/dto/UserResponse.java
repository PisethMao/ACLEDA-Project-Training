package com.acleda.training.studentmanagement.features.auth.dto;

import com.acleda.training.studentmanagement.features.auth.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        UserRole role,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}