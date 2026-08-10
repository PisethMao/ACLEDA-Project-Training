package com.acleda.training.studentmanagement.features.department;

import com.acleda.training.studentmanagement.features.department.dto.CreateDepartmentRequest;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentResponse;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentUpdateResult;
import com.acleda.training.studentmanagement.features.department.dto.UpdateDepartmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DepartmentService {
    DepartmentResponse createDepartment(
            CreateDepartmentRequest request
    );

    Page<DepartmentResponse> getDepartments(
            String keyword,
            Boolean enabled,
            Pageable pageable
    );

    DepartmentResponse getDepartmentById(
            UUID departmentId
    );

    DepartmentUpdateResult updateDepartment(
            UUID departmentId,
            UpdateDepartmentRequest request
    );

    DepartmentResponse updateDepartmentStatus(
            UUID departmentId,
            Boolean enabled
    );

    void deleteDepartment(
            UUID departmentId
    );
}