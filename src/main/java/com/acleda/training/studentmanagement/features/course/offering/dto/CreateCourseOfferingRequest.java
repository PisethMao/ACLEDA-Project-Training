package com.acleda.training.studentmanagement.features.course.offering.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record CreateCourseOfferingRequest(
        @NotNull(message = "Course ID is required")
        UUID courseId,
        @NotNull(message = "Instructor ID is required")
        UUID instructorId,
        @NotBlank(message = "Academic year is required")
        @Size(max = 20, message = "Academic year must not exceed 20 characters")
        String academicYear,
        @NotBlank(message = "Semester is required")
        @Size(max = 30, message = "Semester must not exceed 30 characters")
        String semester,
        @NotBlank(message = "Section is required")
        @Size(max = 20, message = "Section must not exceed 20 characters")
        String section,
        @Size(max = 50, message = "Room must not exceed 50 characters")
        String room,
        @NotNull(message = "Capacity is required")
        @Min(
                value = 1,
                message = "Capacity must be at least 1"
        )
        @Max(
                value = 500,
                message = "Capacity must not exceed 500"
        )
        Integer capacity,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        LocalDate endDate
) {
}
