package com.acleda.training.studentmanagement.features.course;

import com.acleda.training.studentmanagement.features.department.Department;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "courses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_courses_code",
                        columnNames = "code"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(
            nullable = false,
            unique = true,
            length = 50
    )
    private String code;
    @Column(
            nullable = false,
            length = 150
    )
    private String name;
    @Column(length = 500)
    private String description;
    @Column(nullable = false)
    private Integer credit;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "department_id",
            nullable = false
    )
    private Department department;
    @Column(nullable = false)
    private Boolean deleted = false;
    @Column(
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
    @Column(nullable = false)
    private Instant updatedAt;
    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
        if (deleted == null) {
            deleted = false;
        }
    }
    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}