package com.acleda.training.studentmanagement.features.enrollment.dto;

import com.acleda.training.studentmanagement.features.enrollment.EnrollmentStatus;
import com.acleda.training.studentmanagement.features.enrollment.Semester;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateEnrollmentRequest(
        @NotNull(message = "Student ID is required")
        UUID studentId,
        @NotNull(message = "Course ID is required")
        UUID courseId,
        @NotNull(message = "Semester is required")
        Semester semester,
        @NotBlank(message = "Academic year is required")
        @Pattern(
                regexp = "^\\d{4}-\\d{4}$",
                message = "Academic year must follow format YYYY-YYYY"
        )
        String academicYear,
        @NotNull(message = "Enrollment date is required")
        LocalDate enrollmentDate,
        @NotNull(message = "Status is required")
        EnrollmentStatus status
) {
}