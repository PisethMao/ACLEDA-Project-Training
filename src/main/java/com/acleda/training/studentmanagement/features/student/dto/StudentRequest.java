package com.acleda.training.studentmanagement.features.student.dto;

import com.acleda.training.studentmanagement.features.student.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

        // The same field
//public record StudentRequest(
//        @NotBlank(message = "Student code is required")
//        @Size(max = 30, message = "Student code must not exceed 30 characters")
//        String studentCode,
//        @NotBlank(message = "First name is required")
//        @Size(max = 100)
//        String firstName,
//        @NotBlank(message = "Last name is required")
//        @Size(max = 100)
//        String lastName,
//        @NotNull(message = "Gender is required")
//        Gender gender,
//        @NotNull(message = "Date of birth is required")
//        @Past(message = "Date of birth must be in the past")
//        LocalDate dateOfBirth,
//        @NotBlank(message = "Email is required")
//        @Email(message = "Email format is invalid")
//        @Size(max = 150)
//        String email,
//        @Size(max = 30)
//        String phoneNumber,
//        @Size(max = 500)
//        String address,
//        @NotNull(message = "Enrollment date is required")
//        LocalDate enrolledAt
//) {
//}

        // Difference Field
public record StudentRequest(
        @NotBlank(message = "Student code is required")
        @Size(max = 30)
        String code,
        @NotBlank(message = "Given name is required")
        @Size(max = 100)
        String givenName,
        @NotBlank(message = "Family name is required")
        @Size(max = 100)
        String familyName,
        @NotNull(message = "Gender is required")
        Gender sex,
        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be in the past")
        LocalDate birthDate,
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 150)
        String emailAddress,
        @Size(max = 30)
        String phone,
        @Size(max = 500)
        String homeAddress,
        @NotNull(message = "Enrollment date is required")
        LocalDate enrollmentDate
) {
}