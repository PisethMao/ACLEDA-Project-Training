package com.acleda.training.studentmanagement.features.enrollment.dto;

import com.acleda.training.studentmanagement.features.enrollment.EnrollmentStatus;
import com.acleda.training.studentmanagement.features.enrollment.Semester;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EnrollmentResponse(
        UUID id,
        UUID studentId,
        String studentCode,
        String studentName,
        UUID courseId,
        String courseCode,
        String courseName,
        Semester semester,
        String academicYear,
        LocalDate enrollmentDate,
        EnrollmentStatus status,
        Double score,
        String grade,
        Instant createdAt,
        Instant updatedAt
) {
}