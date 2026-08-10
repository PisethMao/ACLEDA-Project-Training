package com.acleda.training.studentmanagement.features.department.dto;

import java.time.Instant;
import java.util.UUID;

public record DepartmentResponse(
        UUID id,
        String code,
        String name,
        String description,
        Boolean enabled,
        Instant createdAt,
        Instant updatedAt
) {
}