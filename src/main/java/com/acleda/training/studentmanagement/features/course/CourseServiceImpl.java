package com.acleda.training.studentmanagement.features.course;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.course.dto.CourseResponse;
import com.acleda.training.studentmanagement.features.course.dto.CreateCourseRequest;
import com.acleda.training.studentmanagement.features.course.dto.UpdateCourseRequest;
import com.acleda.training.studentmanagement.features.department.Department;
import com.acleda.training.studentmanagement.features.department.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl
        implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    @CachePut(
            value = "courses",
            key = "#result.id()"
    )
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
                request.code().trim().toUpperCase()
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
    @Cacheable(
            value = "courses",
            key = "#courseId"
    )
    public CourseResponse getCourseById(
            UUID courseId
    ) {
        Course course =
                findCourse(courseId);
        return courseMapper.toResponse(
                course
        );
    }

    @Override
    @Transactional
    @CachePut(
            value = "courses",
            key = "#courseId"
    )
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
        return courseMapper.toResponse(
                updatedCourse
        );
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "courses",
            key = "#courseId"
    )
    public void deleteCourse(
            UUID courseId
    ) {
        Course course =
                findCourse(courseId);
        course.setDeleted(true);
        courseRepository.save(course);
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
                        () -> new ResourceNotFoundException(
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