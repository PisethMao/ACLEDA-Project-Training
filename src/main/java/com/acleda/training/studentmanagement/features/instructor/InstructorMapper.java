package com.acleda.training.studentmanagement.features.instructor;

import com.acleda.training.studentmanagement.features.instructor.dto.CreateInstructorRequest;
import com.acleda.training.studentmanagement.features.instructor.dto.InstructorResponse;
import com.acleda.training.studentmanagement.features.instructor.dto.UpdateInstructorRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface InstructorMapper {
    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "department",
            ignore = true
    )
    @Mapping(
            target = "enabled",
            ignore = true
    )
    @Mapping(
            target = "deleted",
            ignore = true
    )
    @Mapping(
            target = "createdAt",
            ignore = true
    )
    @Mapping(
            target = "updatedAt",
            ignore = true
    )
    Instructor toEntity(
            CreateInstructorRequest request
    );

    @Mapping(
            target = "id",
            ignore = true
    )
    @Mapping(
            target = "department",
            ignore = true
    )
    @Mapping(
            target = "deleted",
            ignore = true
    )
    @Mapping(
            target = "createdAt",
            ignore = true
    )
    @Mapping(
            target = "updatedAt",
            ignore = true
    )
    void updateEntity(
            UpdateInstructorRequest request,
            @MappingTarget Instructor instructor
    );

    @Mapping(
            source = "department.id",
            target = "departmentId"
    )
    @Mapping(
            source = "department.code",
            target = "departmentCode"
    )
    @Mapping(
            source = "department.name",
            target = "departmentName"
    )
    InstructorResponse toResponse(
            Instructor instructor
    );
}