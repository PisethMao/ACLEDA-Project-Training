package com.acleda.training.studentmanagement.features.instructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InstructorRepository
        extends JpaRepository<Instructor, UUID> {
    Optional<Instructor> findByIdAndDeletedFalse(
            UUID id
    );

    boolean existsByInstructorCodeIgnoreCaseAndDeletedFalse(
            String instructorCode
    );

    boolean existsByEmailIgnoreCaseAndDeletedFalse(
            String email
    );

    boolean existsByInstructorCodeIgnoreCaseAndDeletedFalseAndIdNot(
            String instructorCode,
            UUID id
    );

    boolean existsByEmailIgnoreCaseAndDeletedFalseAndIdNot(
            String email,
            UUID id
    );

    @Query("""
            SELECT i
            FROM Instructor i
            JOIN i.department d
            WHERE i.deleted = false
              AND (
                    :keyword = ''
                    OR LOWER(i.instructorCode)
                        LIKE CONCAT('%', LOWER(:keyword), '%')
                    OR LOWER(i.firstName)
                        LIKE CONCAT('%', LOWER(:keyword), '%')
                    OR LOWER(i.lastName)
                        LIKE CONCAT('%', LOWER(:keyword), '%')
                    OR LOWER(i.email)
                        LIKE CONCAT('%', LOWER(:keyword), '%')
                    OR LOWER(i.specialization)
                        LIKE CONCAT('%', LOWER(:keyword), '%')
              )
              AND (
                    :departmentId IS NULL
                    OR d.id = :departmentId
              )
              AND (
                    :enabled IS NULL
                    OR i.enabled = :enabled
              )
            """)
    Page<Instructor> findAllWithFilters(
            @Param("keyword")
            String keyword,
            @Param("departmentId")
            UUID departmentId,
            @Param("enabled")
            Boolean enabled,
            Pageable pageable
    );
}