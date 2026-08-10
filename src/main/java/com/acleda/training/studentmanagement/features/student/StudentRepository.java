package com.acleda.training.studentmanagement.features.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Page<Student> findAllByDeletedFalse(Pageable pageable);

    Optional<Student> findByIdAndDeletedFalse(UUID id);

    boolean existsByStudentCodeIgnoreCase(String studentCode);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByStudentCodeIgnoreCaseAndIdNot(
            String studentCode,
            UUID id
    );

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            UUID id
    );
}