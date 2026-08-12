package com.acleda.training.studentmanagement.features.course.offering.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateCourseOfferingRequest(
        @NotNull(message = "Course ID is required")
        UUID courseId,
        @NotNull(message = "Instructor ID is required")
        UUID instructorId,
        @NotBlank(message = "Academic year is required")
        @Size(max = 20)
        String academicYear,
        @NotBlank(message = "Semester is required")
        @Size(max = 30)
        String semester,
        @NotBlank(message = "Section is required")
        @Size(max = 20)
        String section,
        @Size(max = 50)
        String room,
        @NotNull(message = "Capacity is required")
        @Min(1)
        @Max(500)
        Integer capacity,
        @NotNull(message = "Start date is required")
        LocalDate startDate,
        @NotNull(message = "End date is required")
        LocalDate endDate,
        @NotNull(message = "Enabled is required")
        Boolean enabled
) {
}