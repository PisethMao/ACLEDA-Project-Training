package com.acleda.training.studentmanagement.features.student.dto;

public record StudentUpdateResult(
        StudentResponse data,
        Boolean isChanged
) {
}
