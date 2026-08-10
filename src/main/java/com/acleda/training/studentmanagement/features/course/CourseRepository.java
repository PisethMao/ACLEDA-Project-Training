package com.acleda.training.studentmanagement.features.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository
        extends JpaRepository<Course, UUID> {
    boolean existsByCodeIgnoreCaseAndDeletedFalse(
            String code
    );

    boolean existsByCodeIgnoreCaseAndIdNotAndDeletedFalse(
            String code,
            UUID id
    );

    Optional<Course> findByIdAndDeletedFalse(
            UUID id
    );

    @Query("""
            SELECT c
            FROM Course c
            WHERE c.deleted = false
              AND (
                    :departmentId IS NULL
                    OR c.department.id = :departmentId
              )
              AND (
                    :credit IS NULL
                    OR c.credit = :credit
              )
              AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(c.code)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(c.name)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(c.description, ''))
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Course> searchCourses(
            @Param("keyword")
            String keyword,
            @Param("departmentId")
            UUID departmentId,
            @Param("credit")
            Integer credit,
            Pageable pageable
    );
}