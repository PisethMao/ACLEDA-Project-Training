package com.acleda.training.studentmanagement.features.course.offering;

import com.acleda.training.studentmanagement.features.course.Course;
import com.acleda.training.studentmanagement.features.instructor.Instructor;
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
        name = "course_offerings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_course_offerings_course_year_semester_section",
                        columnNames = {
                                "course_id",
                                "academic_year",
                                "semester",
                                "section"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_course_offerings_course",
                        columnList = "course_id"
                ),
                @Index(
                        name = "idx_course_offerings_instructor",
                        columnList = "instructor_id"
                ),
                @Index(
                        name = "idx_course_offerings_academic_year",
                        columnList = "academic_year"
                ),
                @Index(
                        name = "idx_course_offerings_semester",
                        columnList = "semester"
                ),
                @Index(
                        name = "idx_course_offerings_deleted",
                        columnList = "deleted"
                )
        }
)
public class CourseOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "course_id",
            nullable = false
    )
    private Course course;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "instructor_id",
            nullable = false
    )
    private Instructor instructor;
    @Column(
            name = "academic_year",
            nullable = false,
            length = 20
    )
    private String academicYear;
    @Column(
            name = "semester",
            nullable = false,
            length = 30
    )
    private String semester;
    @Column(
            name = "section",
            nullable = false,
            length = 20
    )
    private String section;
    @Column(
            name = "room",
            length = 50
    )
    private String room;
    @Column(
            name = "capacity",
            nullable = false
    )
    private Integer capacity;
    @Column(
            name = "start_date",
            nullable = false
    )
    private LocalDate startDate;
    @Column(
            name = "end_date",
            nullable = false
    )
    private LocalDate endDate;
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
