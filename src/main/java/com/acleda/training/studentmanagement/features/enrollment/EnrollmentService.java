package com.acleda.training.studentmanagement.features.enrollment;

import com.acleda.training.studentmanagement.features.enrollment.dto.CreateEnrollmentRequest;
import com.acleda.training.studentmanagement.features.enrollment.dto.EnrollmentResponse;
import com.acleda.training.studentmanagement.features.enrollment.dto.UpdateEnrollmentRequest;
import com.acleda.training.studentmanagement.features.enrollment.dto.UpdateEnrollmentResultRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EnrollmentService {
    EnrollmentResponse createEnrollment(
            CreateEnrollmentRequest request
    );

    Page<EnrollmentResponse> getEnrollments(
            String keyword,
            UUID studentId,
            UUID courseId,
            Semester semester,
            String academicYear,
            EnrollmentStatus status,
            Pageable pageable
    );

    EnrollmentResponse getEnrollmentById(
            UUID enrollmentId
    );

    EnrollmentResponse updateEnrollment(
            UUID enrollmentId,
            UpdateEnrollmentRequest request
    );

    EnrollmentResponse updateEnrollmentResult(
            UUID enrollmentId,
            UpdateEnrollmentResultRequest request
    );

    void deleteEnrollment(
            UUID enrollmentId
    );
}