package com.acleda.training.studentmanagement.features.instructor;

import com.acleda.training.studentmanagement.exception.ApiResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.instructor.dto.CreateInstructorRequest;
import com.acleda.training.studentmanagement.features.instructor.dto.InstructorResponse;
import com.acleda.training.studentmanagement.features.instructor.dto.UpdateInstructorRequest;
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
@RequestMapping("/api/v1/instructors")
@RequiredArgsConstructor
@Tag(
        name = "Instructor",
        description = "APIs for managing instructors"
)
public class InstructorController {
    private final InstructorService instructorService;

    @Operation(
            summary = "Create instructor",
            description = "Creates a new instructor"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<InstructorResponse>>
    createInstructor(
            @Valid
            @RequestBody
            CreateInstructorRequest request,
            HttpServletRequest httpServletRequest
    ) {
        InstructorResponse response =
                instructorService.createInstructor(
                        request
                );
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "Instructor created successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get all instructors",
            description = "Get all instructors in departments"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<InstructorResponse>>>
    getInstructors(
            @RequestParam(required = false)
            String keyword,
            @RequestParam(required = false)
            UUID departmentId,
            @RequestParam(required = false)
            Boolean enabled,
            @ParameterObject
            @PageableDefault(
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable,
            HttpServletRequest httpServletRequest
    ) {
        Page<InstructorResponse> response =
                instructorService.getInstructors(
                        keyword,
                        departmentId,
                        enabled,
                        pageable
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Instructors retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get instructor by ID",
            description = "Retrieves an instructor by ID"
    )
    @GetMapping("/{instructorId}")
    public ResponseEntity<ApiResponse<InstructorResponse>>
    getInstructorById(
            @PathVariable
            UUID instructorId,
            HttpServletRequest httpServletRequest
    ) {
        InstructorResponse response =
                instructorService.getInstructorById(
                        instructorId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Instructor retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update instructor",
            description = "Updates an existing instructor"
    )
    @PutMapping("/{instructorId}")
    public ResponseEntity<ApiResponse<InstructorResponse>>
    updateInstructor(
            @PathVariable
            UUID instructorId,
            @Valid
            @RequestBody
            UpdateInstructorRequest request,
            HttpServletRequest httpServletRequest
    ) {
        InstructorResponse response =
                instructorService.updateInstructor(
                        instructorId,
                        request
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Instructor updated successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Delete instructor",
            description = "Soft deletes an instructor"
    )
    @DeleteMapping("/{instructorId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteInstructor(
            @PathVariable
            UUID instructorId,
            HttpServletRequest httpServletRequest
    ) {
        instructorService.deleteInstructor(
                instructorId
        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Instructor deleted successfully",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}