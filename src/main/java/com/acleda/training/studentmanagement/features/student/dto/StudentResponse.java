package com.acleda.training.studentmanagement.features.student.dto;

import com.acleda.training.studentmanagement.features.student.Gender;
import com.acleda.training.studentmanagement.features.student.StudentStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

    // Map less field than entity
//public record StudentResponse(
//        UUID id,
//        String studentCode,
//        String firstName,
//        String lastName,
//        String fullName,
//        Gender gender,
//        LocalDate dateOfBirth,
//        String email,
//        String phoneNumber,
//        String address,
//        StudentStatus status,
//        LocalDate enrolledAt,
//        Instant createdAt,
//        Instant updatedAt
//) {
//}

    // Map many field that entity don't have
public record StudentResponse(
        UUID id,
        String studentCode,
        String firstName,
        String lastName,
        String fullName,
        String displayCode,
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