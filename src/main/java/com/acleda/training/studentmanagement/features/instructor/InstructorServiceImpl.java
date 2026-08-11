package com.acleda.training.studentmanagement.features.instructor;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.department.Department;
import com.acleda.training.studentmanagement.features.department.DepartmentRepository;
import com.acleda.training.studentmanagement.features.instructor.dto.CreateInstructorRequest;
import com.acleda.training.studentmanagement.features.instructor.dto.InstructorResponse;
import com.acleda.training.studentmanagement.features.instructor.dto.UpdateInstructorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl
        implements InstructorService {
    private final InstructorRepository instructorRepository;
    private final DepartmentRepository departmentRepository;
    private final InstructorMapper instructorMapper;

    @Override
    @Transactional
    public InstructorResponse createInstructor(
            CreateInstructorRequest request
    ) {
        String instructorCode =
                normalizeInstructorCode(
                        request.instructorCode()
                );
        String email =
                normalizeEmail(
                        request.email()
                );
        validateCreateDuplicates(
                instructorCode,
                email
        );
        Department department =
                getDepartment(
                        request.departmentId()
                );
        Instructor instructor =
                instructorMapper.toEntity(
                        request
                );
        instructor.setInstructorCode(
                instructorCode
        );
        instructor.setFirstName(
                normalizeText(
                        request.firstName()
                )
        );
        instructor.setLastName(
                normalizeText(
                        request.lastName()
                )
        );
        instructor.setEmail(
                email
        );
        instructor.setPhone(
                normalizeNullableText(
                        request.phone()
                )
        );
        instructor.setSpecialization(
                normalizeNullableText(
                        request.specialization()
                )
        );
        instructor.setDepartment(
                department
        );
        instructor.setEnabled(
                true
        );
        instructor.setDeleted(
                false
        );
        Instructor savedInstructor =
                instructorRepository.save(
                        instructor
                );
        return instructorMapper.toResponse(
                savedInstructor
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InstructorResponse> getInstructors(
            String keyword,
            UUID departmentId,
            Boolean enabled,
            Pageable pageable
    ) {
        String normalizedKeyword =
                normalizeSearchKeyword(
                        keyword
                );
        Page<Instructor> instructors =
                instructorRepository.findAllWithFilters(
                        normalizedKeyword,
                        departmentId,
                        enabled,
                        pageable
                );
        return instructors.map(
                instructorMapper::toResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InstructorResponse getInstructorById(
            UUID instructorId
    ) {
        Instructor instructor =
                getInstructor(
                        instructorId
                );
        return instructorMapper.toResponse(
                instructor
        );
    }

    @Override
    @Transactional
    public InstructorResponse updateInstructor(
            UUID instructorId,
            UpdateInstructorRequest request
    ) {
        Instructor instructor =
                getInstructor(
                        instructorId
                );
        String instructorCode =
                normalizeInstructorCode(
                        request.instructorCode()
                );
        String email =
                normalizeEmail(
                        request.email()
                );
        validateUpdateDuplicates(
                instructorId,
                instructorCode,
                email
        );
        Department department =
                getDepartment(
                        request.departmentId()
                );
        instructorMapper.updateEntity(
                request,
                instructor
        );
        instructor.setInstructorCode(
                instructorCode
        );
        instructor.setFirstName(
                normalizeText(
                        request.firstName()
                )
        );
        instructor.setLastName(
                normalizeText(
                        request.lastName()
                )
        );
        instructor.setEmail(
                email
        );
        instructor.setPhone(
                normalizeNullableText(
                        request.phone()
                )
        );
        instructor.setSpecialization(
                normalizeNullableText(
                        request.specialization()
                )
        );
        instructor.setDepartment(
                department
        );
        instructor.setEnabled(
                request.enabled()
        );
        Instructor updatedInstructor =
                instructorRepository.save(
                        instructor
                );
        return instructorMapper.toResponse(
                updatedInstructor
        );
    }

    @Override
    @Transactional
    public void deleteInstructor(
            UUID instructorId
    ) {
        Instructor instructor =
                getInstructor(
                        instructorId
                );
        instructor.setDeleted(
                true
        );
        instructor.setEnabled(
                false
        );
        instructorRepository.save(
                instructor
        );
    }

    private Instructor getInstructor(
            UUID instructorId
    ) {
        return instructorRepository
                .findByIdAndDeletedFalse(
                        instructorId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Instructor not found with ID: "
                                                + instructorId
                                )
                );
    }

    private Department getDepartment(
            UUID departmentId
    ) {
        return departmentRepository
                .findByIdAndDeletedFalse(
                        departmentId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Department not found with ID: "
                                                + departmentId
                                )
                );
    }

    private void validateCreateDuplicates(
            String instructorCode,
            String email
    ) {
        boolean instructorCodeExists =
                instructorRepository
                        .existsByInstructorCodeIgnoreCaseAndDeletedFalse(
                                instructorCode
                        );
        if (instructorCodeExists) {
            throw new ConflictException(
                    "Instructor code already exists: "
                            + instructorCode
            );
        }
        boolean emailExists =
                instructorRepository
                        .existsByEmailIgnoreCaseAndDeletedFalse(
                                email
                        );
        if (emailExists) {
            throw new ConflictException(
                    "Instructor email already exists: "
                            + email
            );
        }
    }

    private void validateUpdateDuplicates(
            UUID instructorId,
            String instructorCode,
            String email
    ) {
        boolean instructorCodeExists =
                instructorRepository
                        .existsByInstructorCodeIgnoreCaseAndDeletedFalseAndIdNot(
                                instructorCode,
                                instructorId
                        );
        if (instructorCodeExists) {
            throw new ConflictException(
                    "Instructor code already exists: "
                            + instructorCode
            );
        }
        boolean emailExists =
                instructorRepository
                        .existsByEmailIgnoreCaseAndDeletedFalseAndIdNot(
                                email,
                                instructorId
                        );
        if (emailExists) {
            throw new ConflictException(
                    "Instructor email already exists: "
                            + email
            );
        }
    }

    private String normalizeInstructorCode(
            String instructorCode
    ) {
        return instructorCode
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private String normalizeEmail(
            String email
    ) {
        return email
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String normalizeText(
            String value
    ) {
        return value.trim();
    }

    private String normalizeNullableText(
            String value
    ) {
        if (value == null) {
            return null;
        }
        String normalized =
                value.trim();
        if (normalized.isEmpty()) {
            return null;
        }
        return normalized;
    }

    private String normalizeSearchKeyword(
            String keyword
    ) {
        if (keyword == null) {
            return "";
        }
        return keyword.trim();
    }
}