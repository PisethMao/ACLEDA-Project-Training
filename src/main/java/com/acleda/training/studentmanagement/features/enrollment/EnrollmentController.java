package com.acleda.training.studentmanagement.features.enrollment;

import com.acleda.training.studentmanagement.exception.ApiErrorResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.enrollment.dto.CreateEnrollmentRequest;
import com.acleda.training.studentmanagement.features.enrollment.dto.EnrollmentResponse;
import com.acleda.training.studentmanagement.features.enrollment.dto.UpdateEnrollmentRequest;
import com.acleda.training.studentmanagement.features.enrollment.dto.UpdateEnrollmentResultRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Tag(
        name = "Enrollment",
        description = "APIs for managing student course enrollments"
)
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @Operation(
            summary = "Create enrollment",
            description = "Enrolls a student into a course"
    )
    @PostMapping
    public ResponseEntity<ApiErrorResponse<EnrollmentResponse>>
    createEnrollment(
            @Valid
            @RequestBody
            CreateEnrollmentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        EnrollmentResponse response =
                enrollmentService.createEnrollment(
                        request
                );
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "Enrollment created successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get all enrollments",
            description = """
                Search, filter, sort, and paginate enrollments
                """
    )
    @GetMapping
    public ResponseEntity<ApiErrorResponse<Page<EnrollmentResponse>>>
    getEnrollments(
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            UUID studentId,
            @RequestParam(required = false)
            UUID courseId,
            @RequestParam(required = false)
            Semester semester,
            @RequestParam(required = false)
            String academicYear,
            @RequestParam(required = false)
            EnrollmentStatus status,
            @ParameterObject
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<EnrollmentResponse> response =
                enrollmentService.getEnrollments(
                        keyword,
                        studentId,
                        courseId,
                        semester,
                        academicYear,
                        status,
                        pageable
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Enrollments retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get enrollment by ID",
            description = "Retrieves a specific enrollment"
    )
    @GetMapping("/{enrollmentId}")
    public ResponseEntity<ApiErrorResponse<EnrollmentResponse>>
    getEnrollmentById(
            @PathVariable
            UUID enrollmentId,
            HttpServletRequest httpServletRequest
    ) {
        EnrollmentResponse response =
                enrollmentService.getEnrollmentById(
                        enrollmentId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Enrollment retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update enrollment",
            description = "Updates enrollment information"
    )
    @PutMapping("/{enrollmentId}")
    public ResponseEntity<ApiErrorResponse<EnrollmentResponse>>
    updateEnrollment(
            @PathVariable
            UUID enrollmentId,
            @Valid
            @RequestBody
            UpdateEnrollmentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        EnrollmentResponse response =
                enrollmentService.updateEnrollment(
                        enrollmentId,
                        request
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Enrollment updated successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update enrollment result",
            description = "Updates score, grade, and enrollment status"
    )
    @PatchMapping("/{enrollmentId}/result")
    public ResponseEntity<ApiErrorResponse<EnrollmentResponse>>
    updateEnrollmentResult(
            @PathVariable
            UUID enrollmentId,
            @Valid
            @RequestBody
            UpdateEnrollmentResultRequest request,
            HttpServletRequest httpServletRequest
    ) {
        EnrollmentResponse response =
                enrollmentService.updateEnrollmentResult(
                        enrollmentId,
                        request
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Enrollment result updated successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Delete enrollment",
            description = "Soft deletes an enrollment"
    )
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<ApiErrorResponse<Object>>
    deleteEnrollment(
            @PathVariable
            UUID enrollmentId,
            HttpServletRequest httpServletRequest
    ) {
        enrollmentService.deleteEnrollment(
                enrollmentId
        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Enrollment deleted successfully",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}