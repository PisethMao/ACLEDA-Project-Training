package com.acleda.training.studentmanagement.features.department;

import com.acleda.training.studentmanagement.features.department.dto.CreateDepartmentRequest;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Department toEntity(CreateDepartmentRequest request);

    DepartmentResponse toResponse(Department department);
}