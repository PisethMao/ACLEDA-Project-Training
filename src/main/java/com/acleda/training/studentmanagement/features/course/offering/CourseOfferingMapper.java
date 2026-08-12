package com.acleda.training.studentmanagement.features.course.offering;

import com.acleda.training.studentmanagement.features.course.offering.dto.CourseOfferingResponse;
import com.acleda.training.studentmanagement.features.course.offering.dto.CreateCourseOfferingRequest;
import com.acleda.training.studentmanagement.features.course.offering.dto.UpdateCourseOfferingRequest;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CourseOfferingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CourseOffering toEntity(
            CreateCourseOfferingRequest request
    );

    @Mapping(
            target = "courseId",
            source = "course.id"
    )
    @Mapping(
            target = "courseCode",
            source = "course.code"
    )
    @Mapping(
            target = "courseName",
            source = "course.name"
    )
    @Mapping(
            target = "instructorId",
            source = "instructor.id"
    )
    CourseOfferingResponse toResponse(
            CourseOffering courseOffering
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "instructor", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            UpdateCourseOfferingRequest request,
            @MappingTarget CourseOffering courseOffering
    );
}