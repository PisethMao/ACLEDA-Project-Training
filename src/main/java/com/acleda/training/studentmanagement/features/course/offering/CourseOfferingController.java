package com.acleda.training.studentmanagement.features.course.offering;

import com.acleda.training.studentmanagement.exception.ApiResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.course.offering.dto.CourseOfferingResponse;
import com.acleda.training.studentmanagement.features.course.offering.dto.CreateCourseOfferingRequest;
import com.acleda.training.studentmanagement.features.course.offering.dto.UpdateCourseOfferingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-offerings")
@RequiredArgsConstructor
@Tag(
        name = "Course Offering",
        description = "APIs for managing course offerings"
)
public class CourseOfferingController {
    private final CourseOfferingService courseOfferingService;

    @PostMapping
    @Operation(
            summary = "Create course offering"
    )
    public ResponseEntity<ApiResponse<CourseOfferingResponse>>
    createCourseOffering(
            @Valid
            @RequestBody
            CreateCourseOfferingRequest request,
            HttpServletRequest httpServletRequest
    ) {
        CourseOfferingResponse response =
                courseOfferingService
                        .createCourseOffering(request);
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "Course offering created successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @GetMapping("/{courseOfferingId}")
    @Operation(
            summary = "Get course offering by ID"
    )
    public ResponseEntity<ApiResponse<CourseOfferingResponse>>
    getCourseOfferingById(
            @PathVariable
            UUID courseOfferingId,
            HttpServletRequest httpServletRequest
    ) {
        CourseOfferingResponse response =
                courseOfferingService
                        .getCourseOfferingById(
                                courseOfferingId
                        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course offering retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @GetMapping
    @Operation(
            summary = "Get course offerings"
    )
    public ResponseEntity<ApiResponse<Page<CourseOfferingResponse>>>
    getCourseOfferings(
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            UUID courseId,
            @RequestParam(required = false)
            UUID instructorId,
            @RequestParam(required = false)
            String academicYear,
            @RequestParam(required = false)
            String semester,
            @ParameterObject
            @PageableDefault(
                    sort = "createdAt"
            )
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<CourseOfferingResponse> response =
                courseOfferingService
                        .getCourseOfferings(
                                keyword,
                                courseId,
                                instructorId,
                                academicYear,
                                semester,
                                pageable
                        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course offerings retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @PutMapping("/{courseOfferingId}")
    @Operation(
            summary = "Update course offering"
    )
    public ResponseEntity<ApiResponse<CourseOfferingResponse>>
    updateCourseOffering(
            @PathVariable
            UUID courseOfferingId,
            @Valid
            @RequestBody
            UpdateCourseOfferingRequest request,
            HttpServletRequest httpServletRequest
    ) {
        CourseOfferingResponse response =
                courseOfferingService
                        .updateCourseOffering(
                                courseOfferingId,
                                request
                        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course offering updated successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @DeleteMapping("/{courseOfferingId}")
    @Operation(
            summary = "Delete course offering"
    )
    public ResponseEntity<ApiResponse<Void>>
    deleteCourseOffering(
            @PathVariable
            UUID courseOfferingId,
            HttpServletRequest httpServletRequest
    ) {
        courseOfferingService
                .deleteCourseOffering(
                        courseOfferingId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course Offering deleted successfully",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}