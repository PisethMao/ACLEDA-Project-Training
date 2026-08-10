package com.acleda.training.studentmanagement.features.department.dto;

public record DepartmentUpdateResult(
        DepartmentResponse data,
        Boolean isChanged
) {
}
