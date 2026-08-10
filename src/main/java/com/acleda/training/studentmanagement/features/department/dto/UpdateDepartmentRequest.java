package com.acleda.training.studentmanagement.features.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateDepartmentRequest(
        @NotBlank(message = "Department code is required")
        @Size(
                max = 30,
                message = "Department code must not exceed 30 characters"
        )
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Department code can contain only letters, numbers, hyphens and underscores"
        )
        String code,
        @NotBlank(message = "Department name is required")
        @Size(
                max = 150,
                message = "Department name must not exceed 150 characters"
        )
        String name,
        @Size(
                max = 500,
                message = "Description must not exceed 500 characters"
        )
        String description
) {
}