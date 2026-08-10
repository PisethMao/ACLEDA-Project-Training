package com.acleda.training.studentmanagement.features.department.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateDepartmentStatusRequest(
        @NotNull(message = "Enabled status is required")
        Boolean enabled
) {
}