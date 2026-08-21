package com.acleda.training.studentmanagement.features.course;

import com.acleda.training.studentmanagement.config.CacheProperties;
import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.course.dto.CourseResponse;
import com.acleda.training.studentmanagement.features.course.dto.CreateCourseRequest;
import com.acleda.training.studentmanagement.features.course.dto.UpdateCourseRequest;
import com.acleda.training.studentmanagement.features.department.Department;
import com.acleda.training.studentmanagement.features.department.DepartmentRepository;
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

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl
        implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final DepartmentRepository departmentRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    @Transactional
    public CourseResponse createCourse(
            CreateCourseRequest request
    ) {
        validateDuplicateCode(
                request.code()
        );
        Department department =
                findDepartment(
                        request.departmentId()
                );
        Course course =
                courseMapper.toEntity(request);
        course.setCode(
                request.code()
                        .trim()
                        .toUpperCase()
        );
        course.setName(
                request.name().trim()
        );
        course.setDepartment(
                department
        );
        Course savedCourse =
                courseRepository.save(course);
        return courseMapper.toResponse(
                savedCourse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getCourses(
            String keyword,
            UUID departmentId,
            Integer credit,
            Pageable pageable
    ) {
        return courseRepository
                .searchCourses(
                        keyword,
                        departmentId,
                        credit,
                        pageable
                )
                .map(courseMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(
            UUID courseId
    ) {
        CourseResponse cachedCourse =
                getCourseFromCache(courseId);
        if (cachedCourse != null) {
            return cachedCourse;
        }
        Course course =
                findCourse(courseId);
        CourseResponse response =
                courseMapper.toResponse(course);
        cacheCourse(
                courseId,
                response
        );
        return response;
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(
            UUID courseId,
            UpdateCourseRequest request
    ) {
        Course course =
                findCourse(courseId);
        validateDuplicateCodeForUpdate(
                courseId,
                request.code()
        );
        Department department =
                findDepartment(
                        request.departmentId()
                );
        courseMapper.updateEntity(
                request,
                course
        );
        course.setCode(
                request.code()
                        .trim()
                        .toUpperCase()
        );
        course.setName(
                request.name().trim()
        );
        course.setDepartment(
                department
        );
        Course updatedCourse =
                courseRepository.save(course);
        evictCourseCache(courseId);
        return courseMapper.toResponse(
                updatedCourse
        );
    }

    @Override
    @Transactional
    public void deleteCourse(
            UUID courseId
    ) {
        Course course =
                findCourse(courseId);
        course.setDeleted(true);
        courseRepository.save(course);
        evictCourseCache(courseId);
    }

    private CourseResponse getCourseFromCache(
            UUID courseId
    ) {
        String key =
                buildCacheKey(courseId);
        try {
            String json =
                    redisTemplate
                            .opsForValue()
                            .get(key);
            if (json == null) {
                log.info(
                        "Course cache MISS - key={}",
                        key
                );
                return null;
            }
            CourseResponse response =
                    objectMapper.readValue(
                            json,
                            CourseResponse.class
                    );
            log.info(
                    "Course cache HIT - key={}",
                    key
            );
            return response;
        } catch (JacksonException e) {
            log.warn(
                    "Invalid Course cache value - key={}",
                    key,
                    e
            );
            evictCourseCache(courseId);
            return null;
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while reading Course - key={}",
                    key,
                    e
            );
            return null;
        }
    }

    private void cacheCourse(
            UUID courseId,
            CourseResponse response
    ) {
        String key =
                buildCacheKey(courseId);
        try {
            String json =
                    objectMapper.writeValueAsString(
                            response
                    );
            redisTemplate
                    .opsForValue()
                    .set(
                            key,
                            json,
                            cacheProperties
                                    .course()
                                    .ttl()
                    );
            log.info(
                    "Course cache SET - key={}, ttl={}",
                    key,
                    cacheProperties
                            .course()
                            .ttl()
            );
        } catch (JacksonException e) {
            log.warn(
                    "Could not serialize Course for Redis - id={}",
                    courseId,
                    e
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while caching Course - key={}",
                    key,
                    e
            );
        }
    }

    private void evictCourseCache(
            UUID courseId
    ) {
        String key =
                buildCacheKey(courseId);
        try {
            Boolean deleted =
                    redisTemplate.delete(key);
            log.info(
                    "Course cache DELETE - key={}, deleted={}",
                    key,
                    deleted
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while deleting Course cache - key={}",
                    key,
                    e
            );
        }
    }

    private String buildCacheKey(
            UUID courseId
    ) {
        return cacheProperties
                .course()
                .keyPrefix()
                + courseId;
    }

    private Course findCourse(
            UUID courseId
    ) {
        return courseRepository
                .findByIdAndDeletedFalse(courseId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Course not found with id: "
                                                + courseId
                                )
                );
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
                                        "Department not found with id: "
                                                + departmentId
                                )
                );
    }

    private void validateDuplicateCode(
            String code
    ) {
        if (courseRepository
                .existsByCodeIgnoreCaseAndDeletedFalse(
                        code.trim()
                )) {
            throw new ConflictException(
                    "Course code already exists"
            );
        }
    }

    private void validateDuplicateCodeForUpdate(
            UUID courseId,
            String code
    ) {
        if (courseRepository
                .existsByCodeIgnoreCaseAndIdNotAndDeletedFalse(
                        code.trim(),
                        courseId
                )) {
            throw new ConflictException(
                    "Course code already exists"
            );
        }
    }
}