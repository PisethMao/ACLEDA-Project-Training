package com.acleda.training.studentmanagement.features.instructor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateInstructorRequest(
        @NotBlank(
                message = "Instructor code is required"
        )
        @Size(
                max = 50,
                message = "Instructor code must not exceed 50 characters"
        )
        @Pattern(
                regexp = "^[A-Za-z0-9_-]+$",
                message = "Instructor code can contain only letters, numbers, underscore, and hyphen"
        )
        String instructorCode,
        @NotBlank(
                message = "First name is required"
        )
        @Size(
                max = 100,
                message = "First name must not exceed 100 characters"
        )
        String firstName,
        @NotBlank(
                message = "Last name is required"
        )
        @Size(
                max = 100,
                message = "Last name must not exceed 100 characters"
        )
        String lastName,
        @NotBlank(
                message = "Email is required"
        )
        @Email(
                message = "Email must be valid"
        )
        @Size(
                max = 150,
                message = "Email must not exceed 150 characters"
        )
        String email,
        @Size(
                max = 30,
                message = "Phone must not exceed 30 characters"
        )
        String phone,
        @Size(
                max = 150,
                message = "Specialization must not exceed 150 characters"
        )
        String specialization,
        @NotNull(
                message = "Department ID is required"
        )
        UUID departmentId,
        @NotNull(
                message = "Enabled status is required"
        )
        Boolean enabled
) {
}