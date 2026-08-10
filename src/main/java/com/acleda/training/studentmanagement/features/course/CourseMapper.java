package com.acleda.training.studentmanagement.features.course;

import com.acleda.training.studentmanagement.features.course.dto.CourseResponse;
import com.acleda.training.studentmanagement.features.course.dto.CreateCourseRequest;
import com.acleda.training.studentmanagement.features.course.dto.UpdateCourseRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    @Mapping(
            target = "departmentId",
            source = "department.id"
    )
    @Mapping(
            target = "departmentCode",
            source = "department.code"
    )
    @Mapping(
            target = "departmentName",
            source = "department.name"
    )
    CourseResponse toResponse(
            Course course
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
    Course toEntity(
            CreateCourseRequest request
    );

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
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
            UpdateCourseRequest request,
            @MappingTarget Course course
    );
}