package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import org.mapstruct.*;

import java.util.Locale;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StudentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentCode", source = "studentCode", qualifiedByName = "trim")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalizeEmail")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trim")
    @Mapping(target = "address", source = "address", qualifiedByName = "trim")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(StudentRequest request);

    @Mapping(
            target = "fullName",
            expression = "java(student.getFirstName() + \" \" + student.getLastName())"
    )
    StudentResponse toResponse(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentCode", source = "studentCode", qualifiedByName = "trim")
    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
    @Mapping(target = "email", source = "email", qualifiedByName = "normalizeEmail")
    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trim")
    @Mapping(target = "address", source = "address", qualifiedByName = "trim")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            StudentRequest request,
            @MappingTarget Student student
    );

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Named("normalizeEmail")
    default String normalizeEmail(String email) {
        return email == null
                ? null
                : email.trim().toLowerCase(Locale.ROOT);
    }
}