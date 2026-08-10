package com.acleda.training.studentmanagement.features.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters"
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username may contain letters, numbers, dots, underscores and hyphens only"
        )
        String username,
        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 72,
                message = "Password must contain between 8 and 72 characters"
        )
        String password
) {
}