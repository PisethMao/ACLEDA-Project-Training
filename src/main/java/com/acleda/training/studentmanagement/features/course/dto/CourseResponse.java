package com.acleda.training.studentmanagement.features.course.dto;

import java.time.Instant;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String code,
        String name,
        String description,
        Integer credit,
        UUID departmentId,
        String departmentCode,
        String departmentName,
        Instant createdAt,
        Instant updatedAt
) {
}