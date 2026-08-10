package com.acleda.training.studentmanagement.features.student.dto;

import com.acleda.training.studentmanagement.features.student.Gender;
import com.acleda.training.studentmanagement.features.student.StudentStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        String studentCode,
        String firstName,
        String lastName,
        String fullName,
        Gender gender,
        LocalDate dateOfBirth,
        String email,
        String phoneNumber,
        String address,
        StudentStatus status,
        LocalDate enrolledAt,
        Instant createdAt,
        Instant updatedAt
) {
}