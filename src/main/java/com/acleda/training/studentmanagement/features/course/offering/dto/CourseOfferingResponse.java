package com.acleda.training.studentmanagement.features.course.offering.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record CourseOfferingResponse(
        UUID id,
        UUID courseId,
        String courseCode,
        String courseName,
        UUID instructorId,
        String academicYear,
        String semester,
        String section,
        String room,
        Integer capacity,
        LocalDate startDate,
        LocalDate endDate,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}
