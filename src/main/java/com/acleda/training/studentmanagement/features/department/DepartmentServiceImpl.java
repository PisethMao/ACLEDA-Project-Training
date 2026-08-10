package com.acleda.training.studentmanagement.features.department;

import com.acleda.training.studentmanagement.exception.BadRequestException;
import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.department.dto.CreateDepartmentRequest;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentResponse;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentUpdateResult;
import com.acleda.training.studentmanagement.features.department.dto.UpdateDepartmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    private String normalizeCode(String code) {
        return code
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeRequiredText(String value) {
        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private void validateCreateDuplicates(
            String code,
            String name
    ) {
        if (departmentRepository.existsByCodeIgnoreCase(code)) {
            throw new ConflictException(
                    "Department code '" + code + "' already exists"
            );
        }
        if (departmentRepository.existsByNameIgnoreCase(name)) {
            throw new ConflictException(
                    "Department name '" + name + "' already exists"
            );
        }
    }

    @Override
    public DepartmentResponse createDepartment(
            CreateDepartmentRequest request
    ) {
        String normalizedCode =
                normalizeCode(request.code());
        String normalizedName =
                normalizeRequiredText(request.name());
        validateCreateDuplicates(
                normalizedCode,
                normalizedName
        );
        Department department =
                departmentMapper.toEntity(request);
        department.setCode(normalizedCode);
        department.setName(normalizedName);
        department.setDescription(
                normalizeOptionalText(request.description())
        );
        department.setEnabled(true);
        department.setDeleted(false);
        Department savedDepartment =
                departmentRepository.saveAndFlush(department);
        return departmentMapper.toResponse(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getDepartments(
            String keyword,
            Boolean enabled,
            Pageable pageable
    ) {
        String normalizedKeyword =
                normalizeOptionalText(keyword);
        return departmentRepository
                .searchDepartments(
                        normalizedKeyword,
                        enabled,
                        pageable
                )
                .map(departmentMapper::toResponse);
    }

    private Department findDepartment(
            UUID departmentId
    ) {
        return departmentRepository
                .findByIdAndDeletedFalse(departmentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Department with ID '"
                                        + departmentId
                                        + "' was not found"
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(
            UUID departmentId
    ) {
        Department department =
                findDepartment(departmentId);
        return departmentMapper.toResponse(department);
    }

    private void validateUpdateDuplicates(
            UUID departmentId,
            String code,
            String name
    ) {
        if (departmentRepository
                .existsByCodeIgnoreCaseAndIdNot(
                        code,
                        departmentId
                )) {
            throw new ConflictException(
                    "Department code '" + code + "' already exists"
            );
        }
        if (departmentRepository
                .existsByNameIgnoreCaseAndIdNot(
                        name,
                        departmentId
                )) {
            throw new ConflictException(
                    "Department name '" + name + "' already exists"
            );
        }
    }

    @Override
    @Transactional
    public DepartmentUpdateResult updateDepartment(
            UUID departmentId,
            UpdateDepartmentRequest request
    ) {
        Department department = findDepartment(departmentId);
        String normalizedCode =
                normalizeCode(request.code());
        String normalizedName =
                normalizeRequiredText(request.name());
        String normalizedDescription =
                normalizeOptionalText(request.description());
        boolean noChanges =
                Objects.equals(department.getCode(), normalizedCode)
                        && Objects.equals(department.getName(), normalizedName)
                        && Objects.equals(
                        department.getDescription(),
                        normalizedDescription
                );
        if (noChanges) {
            return new DepartmentUpdateResult(
                    departmentMapper.toResponse(department),
                    false
            );
        }
        validateUpdateDuplicates(
                departmentId,
                normalizedCode,
                normalizedName
        );
        department.setCode(normalizedCode);
        department.setName(normalizedName);
        department.setDescription(normalizedDescription);
        Department updatedDepartment =
                departmentRepository.saveAndFlush(department);
        return new DepartmentUpdateResult(
                departmentMapper.toResponse(updatedDepartment),
                true
        );
    }

    @Override
    public DepartmentResponse updateDepartmentStatus(
            UUID departmentId,
            Boolean enabled
    ) {
        Department department =
                findDepartment(departmentId);
        if (enabled == null) {
            throw new BadRequestException(
                    "Department status 'enabled' is required"
            );
        }
        if (department.getEnabled().equals(enabled)) {
            throw new BadRequestException(
                    "Department is already "
                            + (enabled ? "enabled" : "disabled")
            );
        }
        department.setEnabled(enabled);
        Department updatedDepartment =
                departmentRepository.saveAndFlush(department);
        return departmentMapper.toResponse(updatedDepartment);
    }

    @Override
    public void deleteDepartment(
            UUID departmentId
    ) {
        Department department =
                findDepartment(departmentId);
        department.setDeleted(true);
        department.setEnabled(false);
        departmentRepository.saveAndFlush(department);
    }
}