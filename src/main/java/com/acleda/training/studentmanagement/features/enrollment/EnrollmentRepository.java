package com.acleda.training.studentmanagement.features.enrollment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository
        extends JpaRepository<Enrollment, UUID> {
    Optional<Enrollment> findByIdAndDeletedFalse(
            UUID id
    );

    boolean existsByStudent_IdAndCourse_IdAndSemesterAndAcademicYearAndDeletedFalse(
            UUID studentId,
            UUID courseId,
            Semester semester,
            String academicYear
    );

    boolean existsByStudent_IdAndCourse_IdAndSemesterAndAcademicYearAndDeletedFalseAndIdNot(
            UUID studentId,
            UUID courseId,
            Semester semester,
            String academicYear,
            UUID id
    );

    @Query(
            value = """
                    SELECT e
                    FROM Enrollment e
                    JOIN FETCH e.student s
                    JOIN FETCH e.course c
                    WHERE e.deleted = false
                      AND (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(s.studentCode)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(s.firstName)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(s.lastName)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(CONCAT(s.firstName, ' ', s.lastName))
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(c.code)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(c.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                      AND (
                            :studentId IS NULL
                            OR s.id = :studentId
                      )
                      AND (
                            :courseId IS NULL
                            OR c.id = :courseId
                      )
                      AND (
                            :semester IS NULL
                            OR e.semester = :semester
                      )
                      AND (
                            :academicYear IS NULL
                            OR :academicYear = ''
                            OR e.academicYear = :academicYear
                      )
                      AND (
                            :status IS NULL
                            OR e.status = :status
                      )
                    """,
            countQuery = """
                    SELECT COUNT(e)
                    FROM Enrollment e
                    JOIN e.student s
                    JOIN e.course c
                    WHERE e.deleted = false
                      AND (
                            :keyword IS NULL
                            OR :keyword = ''
                            OR LOWER(s.studentCode)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(s.firstName)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(s.lastName)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(CONCAT(s.firstName, ' ', s.lastName))
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(c.code)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                            OR LOWER(c.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                      )
                      AND (
                            :studentId IS NULL
                            OR s.id = :studentId
                      )
                      AND (
                            :courseId IS NULL
                            OR c.id = :courseId
                      )
                      AND (
                            :semester IS NULL
                            OR e.semester = :semester
                      )
                      AND (
                            :academicYear IS NULL
                            OR :academicYear = ''
                            OR e.academicYear = :academicYear
                      )
                      AND (
                            :status IS NULL
                            OR e.status = :status
                      )
                    """
    )
    Page<Enrollment> searchEnrollments(
            @Param("keyword")
            String keyword,
            @Param("studentId")
            UUID studentId,
            @Param("courseId")
            UUID courseId,
            @Param("semester")
            Semester semester,
            @Param("academicYear")
            String academicYear,
            @Param("status")
            EnrollmentStatus status,
            Pageable pageable
    );
}