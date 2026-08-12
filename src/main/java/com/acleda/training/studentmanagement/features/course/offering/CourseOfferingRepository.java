package com.acleda.training.studentmanagement.features.course.offering;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CourseOfferingRepository
        extends JpaRepository<CourseOffering, UUID> {
    Optional<CourseOffering> findByIdAndDeletedFalse(
            UUID id
    );

    boolean existsByCourse_IdAndAcademicYearIgnoreCaseAndSemesterIgnoreCaseAndSectionIgnoreCaseAndDeletedFalse(
            UUID courseId,
            String academicYear,
            String semester,
            String section
    );

    boolean existsByCourse_IdAndAcademicYearIgnoreCaseAndSemesterIgnoreCaseAndSectionIgnoreCaseAndDeletedFalseAndIdNot(
            UUID courseId,
            String academicYear,
            String semester,
            String section,
            UUID id
    );

    @Query("""
            SELECT co
            FROM CourseOffering co
            JOIN FETCH co.course c
            JOIN FETCH co.instructor i
            WHERE co.deleted = false
              AND (
                    :courseId IS NULL
                    OR c.id = :courseId
              )
              AND (
                    :instructorId IS NULL
                    OR i.id = :instructorId
              )
              AND (
                    :academicYear IS NULL
                    OR :academicYear = ''
                    OR LOWER(co.academicYear)
                        = LOWER(:academicYear)
              )
              AND (
                    :semester IS NULL
                    OR :semester = ''
                    OR LOWER(co.semester)
                        = LOWER(:semester)
              )
              AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(co.section)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(co.room, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.code)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.name)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<CourseOffering> searchCourseOfferings(
            @Param("keyword")
            String keyword,
            @Param("courseId")
            UUID courseId,
            @Param("instructorId")
            UUID instructorId,
            @Param("academicYear")
            String academicYear,
            @Param("semester")
            String semester,
            Pageable pageable
    );
}
