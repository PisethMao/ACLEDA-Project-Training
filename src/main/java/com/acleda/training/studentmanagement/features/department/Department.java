package com.acleda.training.studentmanagement.features.department;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_departments_code",
                        columnNames = "code"
                ),
                @UniqueConstraint(
                        name = "uk_departments_name",
                        columnNames = "name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_departments_code",
                        columnList = "code"
                ),
                @Index(
                        name = "idx_departments_name",
                        columnList = "name"
                ),
                @Index(
                        name = "idx_departments_enabled",
                        columnList = "enabled"
                )
        }
)
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(
            name = "code",
            nullable = false,
            length = 30
    )
    private String code;
    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;
    @Column(
            name = "description",
            length = 500
    )
    private String description;
    @Column(
            name = "enabled",
            nullable = false
    )
    private Boolean enabled = true;
    @Column(
            name = "deleted",
            nullable = false
    )
    private Boolean deleted = false;
    @CreationTimestamp
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private Instant createdAt;
    @UpdateTimestamp
    @Column(
            name = "updated_at",
            nullable = false
    )
    private Instant updatedAt;
}