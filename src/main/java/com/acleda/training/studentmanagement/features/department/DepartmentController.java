package com.acleda.training.studentmanagement.features.department;

import com.acleda.training.studentmanagement.exception.ApiErrorResponse;
import com.acleda.training.studentmanagement.exception.ResponseUtil;
import com.acleda.training.studentmanagement.features.department.dto.*;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/departments")
@Tag(
        name = "Department",
        description = "APIs for managing departments"
)
public class DepartmentController {
    private final DepartmentService departmentService;

    @Operation(
            summary = "Create department",
            description = "Creates a new department"
    )
    @PostMapping
    public ResponseEntity<ApiErrorResponse<DepartmentResponse>> createDepartment(
            @Valid
            @RequestBody
            CreateDepartmentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        DepartmentResponse response =
                departmentService.createDepartment(request);
        return ResponseUtil.success(
                HttpStatus.CREATED,
                "Department created successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get all departments",
            description = "Retrieves departments with optional keyword, status, pagination, and sorting filters"
    )
    @GetMapping
    public ResponseEntity<ApiErrorResponse<Page<DepartmentResponse>>> getDepartments(
            @RequestParam(required = false)
            String keyword,
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
        Page<DepartmentResponse> response =
                departmentService.getDepartments(
                        keyword,
                        enabled,
                        pageable
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Departments retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Get department by ID",
            description = "Retrieves a department by its unique ID"
    )
    @GetMapping("/{departmentId}")
    public ResponseEntity<ApiErrorResponse<DepartmentResponse>> getDepartmentById(
            @PathVariable
            UUID departmentId,
            HttpServletRequest httpServletRequest
    ) {
        DepartmentResponse response =
                departmentService.getDepartmentById(
                        departmentId
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Department retrieved successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update department",
            description = "Updates an existing department. Returns no changes detected when submitted data is unchanged"
    )
    @PutMapping("/{departmentId}")
    public ResponseEntity<ApiErrorResponse<DepartmentResponse>> updateDepartment(
            @PathVariable
            UUID departmentId,
            @Valid
            @RequestBody
            UpdateDepartmentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        DepartmentUpdateResult result =
                departmentService.updateDepartment(
                        departmentId,
                        request
                );
        String message = result.isChanged()
                ? "Department updated successfully"
                : "No changes detected";
        return ResponseUtil.success(
                HttpStatus.OK,
                message,
                result.data(),
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Update department status",
            description = "Enables or disables a department by its unique ID"
    )
    @PatchMapping("/{departmentId}/status")
    public ResponseEntity<ApiErrorResponse<DepartmentResponse>> updateDepartmentStatus(
            @PathVariable
            UUID departmentId,
            @Valid
            @RequestBody
            UpdateDepartmentStatusRequest request,
            HttpServletRequest httpServletRequest
    ) {
        DepartmentResponse response =
                departmentService.updateDepartmentStatus(
                        departmentId,
                        request.enabled()
                );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Department status updated successfully",
                response,
                httpServletRequest.getRequestURI()
        );
    }

    @Operation(
            summary = "Delete department",
            description = "Soft deletes a department by its unique ID"
    )
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ApiErrorResponse<Void>> deleteDepartment(
            @PathVariable
            UUID departmentId,
            HttpServletRequest httpServletRequest
    ) {
        departmentService.deleteDepartment(
                departmentId
        );
        return ResponseUtil.success(
                HttpStatus.OK,
                "Department deleted successfully",
                null,
                httpServletRequest.getRequestURI()
        );
    }
}