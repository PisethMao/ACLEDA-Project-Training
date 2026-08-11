package com.acleda.training.studentmanagement.features.instructor;

import com.acleda.training.studentmanagement.features.department.Department;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "instructors",
        indexes = {
                @Index(
                        name = "idx_instructors_department_id",
                        columnList = "department_id"
                ),
                @Index(
                        name = "idx_instructors_instructor_code",
                        columnList = "instructor_code"
                ),
                @Index(
                        name = "idx_instructors_email",
                        columnList = "email"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(
            name = "instructor_code",
            nullable = false,
            length = 50
    )
    private String instructorCode;
    @Column(
            name = "first_name",
            nullable = false,
            length = 100
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false,
            length = 100
    )
    private String lastName;
    @Column(
            nullable = false,
            length = 150
    )
    private String email;
    @Column(length = 30)
    private String phone;
    @Column(length = 150)
    private String specialization;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "department_id",
            nullable = false
    )
    private Department department;
    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;
    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (enabled == null) {
            enabled = true;
        }
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}