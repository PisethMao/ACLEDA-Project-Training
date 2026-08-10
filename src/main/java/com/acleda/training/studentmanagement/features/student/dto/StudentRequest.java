package com.acleda.training.studentmanagement.features.student.dto;

import com.acleda.training.studentmanagement.features.student.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record StudentRequest(
        @NotBlank(message = "Student code is required")
        @Size(max = 30, message = "Student code must not exceed 30 characters")
        String studentCode,
        @NotBlank(message = "First name is required")
        @Size(max = 100)
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 100)
        String lastName,
        @NotNull(message = "Gender is required")
        Gender gender,
        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,
        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        @Size(max = 150)
        String email,
        @Size(max = 30)
        String phoneNumber,
        @Size(max = 500)
        String address,
        @NotNull(message = "Enrollment date is required")
        LocalDate enrolledAt
) {
}