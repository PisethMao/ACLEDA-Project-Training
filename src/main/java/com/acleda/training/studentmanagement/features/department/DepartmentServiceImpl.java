package com.acleda.training.studentmanagement.features.department;

import com.acleda.training.studentmanagement.config.CacheProperties;
import com.acleda.training.studentmanagement.exception.BadRequestException;
import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.department.dto.CreateDepartmentRequest;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentResponse;
import com.acleda.training.studentmanagement.features.department.dto.DepartmentUpdateResult;
import com.acleda.training.studentmanagement.features.department.dto.UpdateDepartmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentServiceImpl
        implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

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
                normalizeOptionalText(
                        request.description()
                )
        );
        department.setEnabled(true);
        department.setDeleted(false);
        Department savedDepartment =
                departmentRepository
                        .saveAndFlush(department);
        return departmentMapper.toResponse(
                savedDepartment
        );
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
                .map(
                        departmentMapper::toResponse
                );
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(
            UUID departmentId
    ) {
        DepartmentResponse cachedDepartment =
                getDepartmentFromCache(
                        departmentId
                );
        if (cachedDepartment != null) {
            return cachedDepartment;
        }
        Department department =
                findDepartment(
                        departmentId
                );
        DepartmentResponse response =
                departmentMapper.toResponse(
                        department
                );
        cacheDepartment(
                departmentId,
                response
        );
        return response;
    }

    @Override
    public DepartmentUpdateResult updateDepartment(
            UUID departmentId,
            UpdateDepartmentRequest request
    ) {
        Department department =
                findDepartment(
                        departmentId
                );
        String normalizedCode =
                normalizeCode(
                        request.code()
                );
        String normalizedName =
                normalizeRequiredText(
                        request.name()
                );
        String normalizedDescription =
                normalizeOptionalText(
                        request.description()
                );
        boolean noChanges =
                Objects.equals(
                        department.getCode(),
                        normalizedCode
                )
                        && Objects.equals(
                        department.getName(),
                        normalizedName
                )
                        && Objects.equals(
                        department.getDescription(),
                        normalizedDescription
                );
        if (noChanges) {
            return new DepartmentUpdateResult(
                    departmentMapper.toResponse(
                            department
                    ),
                    false
            );
        }
        validateUpdateDuplicates(
                departmentId,
                normalizedCode,
                normalizedName
        );
        department.setCode(
                normalizedCode
        );
        department.setName(
                normalizedName
        );
        department.setDescription(
                normalizedDescription
        );
        Department updatedDepartment =
                departmentRepository
                        .saveAndFlush(
                                department
                        );
        evictDepartmentCache(
                departmentId
        );
        return new DepartmentUpdateResult(
                departmentMapper.toResponse(
                        updatedDepartment
                ),
                true
        );
    }

    @Override
    public DepartmentResponse updateDepartmentStatus(
            UUID departmentId,
            Boolean enabled
    ) {
        Department department =
                findDepartment(
                        departmentId
                );
        if (enabled == null) {
            throw new BadRequestException(
                    "Department status 'enabled' is required"
            );
        }
        if (department
                .getEnabled()
                .equals(enabled)) {
            throw new BadRequestException(
                    "Department is already "
                            + (
                            enabled
                                    ? "enabled"
                                    : "disabled"
                    )
            );
        }
        department.setEnabled(
                enabled
        );
        Department updatedDepartment =
                departmentRepository
                        .saveAndFlush(
                                department
                        );
        evictDepartmentCache(
                departmentId
        );
        return departmentMapper.toResponse(
                updatedDepartment
        );
    }

    @Override
    public void deleteDepartment(
            UUID departmentId
    ) {
        Department department =
                findDepartment(
                        departmentId
                );
        department.setDeleted(true);
        department.setEnabled(false);
        departmentRepository
                .saveAndFlush(
                        department
                );
        evictDepartmentCache(
                departmentId
        );
    }

    private DepartmentResponse getDepartmentFromCache(
            UUID departmentId
    ) {
        String key =
                buildCacheKey(
                        departmentId
                );
        try {
            String json =
                    redisTemplate
                            .opsForValue()
                            .get(key);
            if (json == null) {
                log.info(
                        "Department cache MISS - key={}",
                        key
                );
                return null;
            }
            DepartmentResponse response =
                    objectMapper.readValue(
                            json,
                            DepartmentResponse.class
                    );
            log.info(
                    "Department cache HIT - key={}",
                    key
            );
            return response;
        } catch (JacksonException e) {
            log.warn(
                    "Invalid Department cache value - key={}",
                    key,
                    e
            );
            evictDepartmentCache(
                    departmentId
            );
            return null;
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while reading Department - key={}",
                    key,
                    e
            );
            return null;
        }
    }

    private void cacheDepartment(
            UUID departmentId,
            DepartmentResponse response
    ) {
        String key =
                buildCacheKey(
                        departmentId
                );
        try {
            String json =
                    objectMapper
                            .writeValueAsString(
                                    response
                            );
            redisTemplate
                    .opsForValue()
                    .set(
                            key,
                            json,
                            cacheProperties
                                    .department()
                                    .ttl()
                    );
            log.info(
                    "Department cache SET - key={}, ttl={}",
                    key,
                    cacheProperties
                            .department()
                            .ttl()
            );
        } catch (JacksonException e) {
            log.warn(
                    "Could not serialize Department for Redis - id={}",
                    departmentId,
                    e
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while caching Department - key={}",
                    key,
                    e
            );
        }
    }

    private void evictDepartmentCache(
            UUID departmentId
    ) {
        String key =
                buildCacheKey(
                        departmentId
                );
        try {
            Boolean deleted =
                    redisTemplate
                            .delete(key);
            log.info(
                    "Department cache DELETE - key={}, deleted={}",
                    key,
                    deleted
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while deleting Department cache - key={}",
                    key,
                    e
            );
        }
    }

    private String buildCacheKey(
            UUID departmentId
    ) {
        return cacheProperties
                .department()
                .keyPrefix()
                + departmentId;
    }

    private Department findDepartment(
            UUID departmentId
    ) {
        return departmentRepository
                .findByIdAndDeletedFalse(
                        departmentId
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Department with ID '"
                                                + departmentId
                                                + "' was not found"
                                )
                );
    }

    private String normalizeCode(
            String code
    ) {
        return code
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private String normalizeRequiredText(
            String value
    ) {
        return value.trim();
    }

    private String normalizeOptionalText(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }
        return value.trim();
    }

    private void validateCreateDuplicates(
            String code,
            String name
    ) {
        if (
                departmentRepository
                        .existsByCodeIgnoreCaseAndDeletedFalse(
                                code
                        )
        ) {
            throw new ConflictException(
                    "Department code '"
                            + code
                            + "' already exists"
            );
        }
        if (
                departmentRepository
                        .existsByNameIgnoreCaseAndDeletedFalse(
                                name
                        )
        ) {
            throw new ConflictException(
                    "Department name '"
                            + name
                            + "' already exists"
            );
        }
    }

    private void validateUpdateDuplicates(
            UUID departmentId,
            String code,
            String name
    ) {
        if (
                departmentRepository
                        .existsByCodeIgnoreCaseAndDeletedFalseAndIdNot(
                                code,
                                departmentId
                        )
        ) {
            throw new ConflictException(
                    "Department code '"
                            + code
                            + "' already exists"
            );
        }
        if (
                departmentRepository
                        .existsByNameIgnoreCaseAndDeletedFalseAndIdNot(
                                name,
                                departmentId
                        )
        ) {
            throw new ConflictException(
                    "Department name '"
                            + name
                            + "' already exists"
            );
        }
    }
}