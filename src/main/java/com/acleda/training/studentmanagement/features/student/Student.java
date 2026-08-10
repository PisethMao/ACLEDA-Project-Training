package com.acleda.training.studentmanagement.features.student;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_students_student_code",
                        columnNames = "student_code"
                ),
                @UniqueConstraint(
                        name = "uk_students_email",
                        columnNames = "email"
                )
        }
)
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "student_code", nullable = false, length = 30)
    private String studentCode;
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    @Column(nullable = false, length = 150)
    private String email;
    @Column(name = "phone_number", length = 30)
    private String phoneNumber;
    @Column(length = 500)
    private String address;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StudentStatus status = StudentStatus.ACTIVE;
    @Column(name = "enrolled_at", nullable = false)
    private LocalDate enrolledAt;
    @Column(nullable = false)
    private boolean deleted = false;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
