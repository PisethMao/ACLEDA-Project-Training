package com.acleda.training.studentmanagement.features.course.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateCourseRequest(
        @NotBlank(message = "Course code is required")
        @Size(
                max = 50,
                message = "Course code must not exceed 50 characters"
        )
        String code,
        @NotBlank(message = "Course name is required")
        @Size(
                max = 150,
                message = "Course name must not exceed 150 characters"
        )
        String name,
        @Size(
                max = 500,
                message = "Description must not exceed 500 characters"
        )
        String description,
        @NotNull(message = "Credit is required")
        @Min(
                value = 1,
                message = "Credit must be at least 1"
        )
        @Max(
                value = 10,
                message = "Credit must not exceed 10"
        )
        Integer credit,
        @NotNull(message = "Department ID is required")
        UUID departmentId
) {
}