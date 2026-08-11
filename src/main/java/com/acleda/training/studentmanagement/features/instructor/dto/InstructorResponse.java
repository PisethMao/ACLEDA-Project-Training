package com.acleda.training.studentmanagement.features.instructor.dto;

import java.time.Instant;
import java.util.UUID;

public record InstructorResponse(
        UUID id,
        String instructorCode,
        String firstName,
        String lastName,
        String email,
        String phone,
        String specialization,
        UUID departmentId,
        String departmentCode,
        String departmentName,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}