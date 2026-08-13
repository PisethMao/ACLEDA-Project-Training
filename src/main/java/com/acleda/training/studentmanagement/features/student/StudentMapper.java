package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import org.mapstruct.*;

import java.util.Locale;

// The same field map
//@Mapper(
//        componentModel = MappingConstants.ComponentModel.SPRING,
//        unmappedTargetPolicy = ReportingPolicy.ERROR
//)
//public interface StudentMapper {
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "studentCode", source = "studentCode", qualifiedByName = "trim")
//    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
//    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
//    @Mapping(target = "email", source = "email", qualifiedByName = "normalizeEmail")
//    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trim")
//    @Mapping(target = "address", source = "address", qualifiedByName = "trim")
//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "deleted", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    Student toEntity(StudentRequest request);
//
//    @Mapping(
//            target = "fullName",
//            expression = "java(student.getFirstName() + \" \" + student.getLastName())"
//    )
//    StudentResponse toResponse(Student student);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "studentCode", source = "studentCode", qualifiedByName = "trim")
//    @Mapping(target = "firstName", source = "firstName", qualifiedByName = "trim")
//    @Mapping(target = "lastName", source = "lastName", qualifiedByName = "trim")
//    @Mapping(target = "email", source = "email", qualifiedByName = "normalizeEmail")
//    @Mapping(target = "phoneNumber", source = "phoneNumber", qualifiedByName = "trim")
//    @Mapping(target = "address", source = "address", qualifiedByName = "trim")
//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "deleted", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    void updateEntity(
//            StudentRequest request,
//            @MappingTarget Student student
//    );
//
//    @Named("trim")
//    default String trim(String value) {
//        return value == null ? null : value.trim();
//    }
//
//    @Named("normalizeEmail")
//    default String normalizeEmail(String email) {
//        return email == null
//                ? null
//                : email.trim().toLowerCase(Locale.ROOT);
//    }
//}

// Difference filed map
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface StudentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "studentCode",
            source = "code",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "firstName",
            source = "givenName",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "lastName",
            source = "familyName",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "gender",
            source = "sex"
    )
    @Mapping(
            target = "dateOfBirth",
            source = "birthDate"
    )
    @Mapping(
            target = "email",
            source = "emailAddress",
            qualifiedByName = "normalizeEmail"
    )
    @Mapping(
            target = "phoneNumber",
            source = "phone",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "address",
            source = "homeAddress",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "enrolledAt",
            source = "enrollmentDate"
    )
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Student toEntity(StudentRequest request);

//    @Mapping(
//            target = "fullName",
//            source = ".",
//            qualifiedByName = "toFullName"
//    )
//    StudentResponse toResponse(Student student);
    @Mapping(
            target = "fullName",
            source = ".",
            qualifiedByName = "toFullName"
    )
    @Mapping(
            target = "displayCode",
            source = ".",
            qualifiedByName = "toDisplayCode"
    )
    StudentResponse toResponse(
            Student student
    );

    @Named("toDisplayCode")
    default String toDisplayCode(
            Student student
    ) {
        if (student == null) {
            return null;
        }
        return student.getStudentCode()
                + " - "
                + student.getFirstName()
                + " "
                + student.getLastName();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(
            target = "studentCode",
            source = "code",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "firstName",
            source = "givenName",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "lastName",
            source = "familyName",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "gender",
            source = "sex"
    )
    @Mapping(
            target = "dateOfBirth",
            source = "birthDate"
    )
    @Mapping(
            target = "email",
            source = "emailAddress",
            qualifiedByName = "normalizeEmail"
    )
    @Mapping(
            target = "phoneNumber",
            source = "phone",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "address",
            source = "homeAddress",
            qualifiedByName = "trim"
    )
    @Mapping(
            target = "enrolledAt",
            source = "enrollmentDate"
    )
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
        return value == null
                ? null
                : value.trim();
    }

    @Named("normalizeEmail")
    default String normalizeEmail(String email) {
        return email == null
                ? null
                : email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    @Named("toFullName")
    default String toFullName(Student student) {
        if (student == null) {
            return null;
        }
        return student.getFirstName()
                + " "
                + student.getLastName();
    }
}