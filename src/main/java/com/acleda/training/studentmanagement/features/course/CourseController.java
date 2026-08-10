package com.acleda.training.studentmanagement.features.course;

import com.acleda.training.studentmanagement.exception.ApiErrorResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.course.dto.CourseResponse;
import com.acleda.training.studentmanagement.features.course.dto.CreateCourseRequest;
import com.acleda.training.studentmanagement.features.course.dto.UpdateCourseRequest;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(
        name = "Course",
        description = "APIs for managing courses"
)
public class CourseController {
    private final CourseService courseService;

    @Operation(
            summary = "Create course",
            description = "Creates a new course"
    )
    @PostMapping
    public ResponseEntity<ApiErrorResponse<CourseResponse>>
    createCourse(
            @Valid
            @RequestBody
            CreateCourseRequest request,
            HttpServletRequest httpServletRequest
    ) {
        CourseResponse course =
                courseService.createCourse(
                        request
                );
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "Course created successfully",
                course,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get all courses",
            description = "Retrieves courses with optional keyword, department, credit, pagination, and sorting filters"
    )
    @GetMapping
    public ResponseEntity<ApiErrorResponse<Page<CourseResponse>>> getCourses(
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            UUID departmentId,
            @RequestParam(required = false)
            Integer credit,
            @ParameterObject
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<CourseResponse> response =
                courseService.getCourses(
                        keyword,
                        departmentId,
                        credit,
                        pageable
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Courses retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get course by ID",
            description = "Retrieves a course by its unique ID"
    )
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiErrorResponse<CourseResponse>>
    getCourseById(
            @PathVariable
            UUID courseId,
            HttpServletRequest httpServletRequest
    ) {
        CourseResponse course =
                courseService.getCourseById(
                        courseId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course retrieved successfully",
                course,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update course",
            description = "Updates an existing course by its unique ID"
    )
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiErrorResponse<CourseResponse>>
    updateCourse(
            @PathVariable
            UUID courseId,
            @Valid
            @RequestBody
            UpdateCourseRequest request,
            HttpServletRequest httpServletRequest
    ) {
        CourseResponse course =
                courseService.updateCourse(
                        courseId,
                        request
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course updated successfully",
                course,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Delete course",
            description = "Soft deletes a course by its unique ID"
    )
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiErrorResponse<Object>> deleteCourse(
            @PathVariable
            UUID courseId,
            HttpServletRequest httpServletRequest
    ) {
        courseService.deleteCourse(courseId);
        return ResponseUtil.success(
                HttpStatus.OK,
                "Course deleted successfully",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}