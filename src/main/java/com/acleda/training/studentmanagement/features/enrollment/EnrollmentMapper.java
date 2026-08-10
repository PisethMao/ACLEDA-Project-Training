package com.acleda.training.studentmanagement.features.enrollment;

import com.acleda.training.studentmanagement.features.enrollment.dto.EnrollmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    @Mapping(
            target = "studentId",
            source = "student.id"
    )
    @Mapping(
            target = "studentCode",
            source = "student.studentCode"
    )
    @Mapping(
            target = "studentName",
            expression = "java(getStudentName(enrollment))"
    )
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
    EnrollmentResponse toResponse(
            Enrollment enrollment
    );

    default String getStudentName(
            Enrollment enrollment
    ) {
        if (enrollment.getStudent() == null) {
            return null;
        }
        return enrollment.getStudent().getFirstName()
                + " "
                + enrollment.getStudent().getLastName();
    }
}