package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.exception.ApiResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import com.acleda.training.studentmanagement.features.student.dto.StudentUpdateResult;
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
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(
        name = "Student",
        description = "APIs for managing students"
)
public class StudentController {
    private final StudentService studentService;

    @Operation(
            summary = "Create student",
            description = "Creates a new student"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid
            @RequestBody
            StudentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        StudentResponse student =
                studentService.createStudent(
                        request
                );
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "Student created successfully",
                student,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get all students",
            description = "Retrieves students with pagination and sorting"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<StudentResponse>>> getStudents(
            @ParameterObject
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<StudentResponse> students =
                studentService.getStudents(
                        pageable
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Students retrieved successfully",
                students,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get student by ID",
            description = "Retrieves a student by its unique ID"
    )
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable
            UUID studentId,
            HttpServletRequest httpServletRequest
    ) {
        StudentResponse student =
                studentService.getStudentById(
                        studentId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Student retrieved successfully",
                student,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update student",
            description = "Updates an existing student. Returns no changes detected when submitted data is unchanged"
    )
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable
            UUID studentId,
            @Valid
            @RequestBody
            StudentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        StudentUpdateResult result =
                studentService.updateStudent(
                        studentId,
                        request
                );
        String message = result.isChanged()
                ? "Student updated successfully"
                : "No changes detected";
        return ResponseUtil.success(
                HttpStatus.OK,
                message,
                result.data(),
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Delete student",
            description = "Soft deletes a student by its unique ID"
    )
    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable
            UUID studentId,
            HttpServletRequest httpServletRequest
    ) {
        studentService.deleteStudent(
                studentId
        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Student deleted successfully",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}