package com.acleda.training.studentmanagement.features.department;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository
        extends JpaRepository<Department, UUID> {
    Optional<Department> findByIdAndDeletedFalse(
            UUID id
    );

    boolean existsByCodeIgnoreCaseAndDeletedFalse(
            String code
    );

    boolean existsByNameIgnoreCaseAndDeletedFalse(
            String name
    );

    boolean existsByCodeIgnoreCaseAndDeletedFalseAndIdNot(
            String code,
            UUID id
    );

    boolean existsByNameIgnoreCaseAndDeletedFalseAndIdNot(
            String name,
            UUID id
    );

    @Query("""
            SELECT d
            FROM Department d
            WHERE d.deleted = false
              AND (
                    :enabled IS NULL
                    OR d.enabled = :enabled
              )
              AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(d.code)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.name)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.description)
                        LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<Department> searchDepartments(
            @Param("keyword")
            String keyword,
            @Param("enabled")
            Boolean enabled,
            Pageable pageable
    );
}