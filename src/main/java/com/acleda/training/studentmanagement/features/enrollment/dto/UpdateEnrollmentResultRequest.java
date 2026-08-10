package com.acleda.training.studentmanagement.features.enrollment.dto;

import com.acleda.training.studentmanagement.features.enrollment.EnrollmentStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateEnrollmentResultRequest(
        @NotNull(message = "Score is required")
        @DecimalMin(
                value = "0.0",
                message = "Score must be at least 0"
        )
        @DecimalMax(
                value = "100.0",
                message = "Score must not exceed 100"
        )
        Double score,
        @Size(
                max = 10,
                message = "Grade must not exceed 10 characters"
        )
        String grade,
        @NotNull(message = "Status is required")
        EnrollmentStatus status
) {
}