package com.acleda.training.studentmanagement.features.enrollment;

import com.acleda.training.studentmanagement.features.course.Course;
import com.acleda.training.studentmanagement.features.student.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "enrollments",
        indexes = {
                @Index(
                        name = "idx_enrollments_student_id",
                        columnList = "student_id"
                ),
                @Index(
                        name = "idx_enrollments_course_id",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_enrollments_academic_year",
                        columnList = "academic_year"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "student_id",
            nullable = false
    )
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "course_id",
            nullable = false
    )
    private Course course;
    @Enumerated(EnumType.STRING)
    @Column(
            name = "semester",
            nullable = false,
            length = 30
    )
    private Semester semester;
    @Column(
            name = "academic_year",
            nullable = false,
            length = 20
    )
    private String academicYear;
    @Column(
            name = "enrollment_date",
            nullable = false
    )
    private LocalDate enrollmentDate;
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 30
    )
    private EnrollmentStatus status;
    @Column(name = "score")
    private Double score;
    @Column(
            name = "grade",
            length = 10
    )
    private String grade;
    @Builder.Default
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