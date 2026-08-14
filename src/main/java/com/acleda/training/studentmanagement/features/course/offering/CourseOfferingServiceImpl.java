package com.acleda.training.studentmanagement.features.course.offering;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.course.Course;
import com.acleda.training.studentmanagement.features.course.CourseRepository;
import com.acleda.training.studentmanagement.features.course.offering.dto.CourseOfferingResponse;
import com.acleda.training.studentmanagement.features.course.offering.dto.CreateCourseOfferingRequest;
import com.acleda.training.studentmanagement.features.course.offering.dto.UpdateCourseOfferingRequest;
import com.acleda.training.studentmanagement.features.instructor.Instructor;
import com.acleda.training.studentmanagement.features.instructor.InstructorRepository;
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
public class CourseOfferingServiceImpl implements CourseOfferingService {
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CourseOfferingMapper courseOfferingMapper;

    @Override
    @Transactional
    @CachePut(
            cacheNames = CourseOfferingCacheNames.BY_ID,
            key = "#result.id()"
    )
    public CourseOfferingResponse createCourseOffering(
            CreateCourseOfferingRequest request
    ) {
        validateDateRange(
                request.startDate(),
                request.endDate()
        );
        Course course = courseRepository
                .findByIdAndDeletedFalse(request.courseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );
        Instructor instructor = instructorRepository
                .findByIdAndDeletedFalse(request.instructorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instructor not found"
                        )
                );
        boolean exists =
                courseOfferingRepository
                        .existsByCourse_IdAndAcademicYearIgnoreCaseAndSemesterIgnoreCaseAndSectionIgnoreCaseAndDeletedFalse(
                                request.courseId(),
                                request.academicYear().trim(),
                                request.semester().trim(),
                                request.section().trim()
                        );
        if (exists) {
            throw new ConflictException(
                    "Course offering already exists for this course, academic year, semester, and section"
            );
        }
        CourseOffering courseOffering =
                courseOfferingMapper.toEntity(request);
        courseOffering.setCourse(course);
        courseOffering.setInstructor(instructor);
        normalize(courseOffering);
        CourseOffering saved =
                courseOfferingRepository.saveAndFlush(courseOffering);
        return courseOfferingMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = CourseOfferingCacheNames.BY_ID,
            key = "#courseOfferingId"
    )
    public CourseOfferingResponse getCourseOfferingById(
            UUID courseOfferingId
    ) {
        CourseOffering courseOffering =
                findCourseOffering(courseOfferingId);
        return courseOfferingMapper.toResponse(
                courseOffering
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseOfferingResponse> getCourseOfferings(
            String keyword,
            UUID courseId,
            UUID instructorId,
            String academicYear,
            String semester,
            Pageable pageable
    ) {
        return courseOfferingRepository
                .searchCourseOfferings(
                        keyword,
                        courseId,
                        instructorId,
                        academicYear,
                        semester,
                        pageable
                )
                .map(courseOfferingMapper::toResponse);
    }

    @Transactional
    @CachePut(
            cacheNames = CourseOfferingCacheNames.BY_ID,
            key = "#courseOfferingId"
    )
    public CourseOfferingResponse updateCourseOffering(
            UUID courseOfferingId,
            UpdateCourseOfferingRequest request
    ) {
        CourseOffering courseOffering =
                findCourseOffering(courseOfferingId);
        validateDateRange(
                request.startDate(),
                request.endDate()
        );
        Course course = courseRepository
                .findByIdAndDeletedFalse(request.courseId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );
        Instructor instructor = instructorRepository
                .findByIdAndDeletedFalse(request.instructorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Instructor not found"
                        )
                );
        boolean exists =
                courseOfferingRepository
                        .existsByCourse_IdAndAcademicYearIgnoreCaseAndSemesterIgnoreCaseAndSectionIgnoreCaseAndDeletedFalseAndIdNot(
                                request.courseId(),
                                request.academicYear().trim(),
                                request.semester().trim(),
                                request.section().trim(),
                                courseOfferingId
                        );
        if (exists) {
            throw new ConflictException(
                    "Course offering already exists for this course, academic year, semester, and section"
            );
        }
        courseOfferingMapper.updateEntity(
                request,
                courseOffering
        );
        courseOffering.setCourse(course);
        courseOffering.setInstructor(instructor);
        normalize(courseOffering);
        CourseOffering saved =
                courseOfferingRepository.save(courseOffering);
        return courseOfferingMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(
            cacheNames = CourseOfferingCacheNames.BY_ID,
            key = "#courseOfferingId"
    )
    public void deleteCourseOffering(
            UUID courseOfferingId
    ) {
        CourseOffering courseOffering =
                findCourseOffering(courseOfferingId);
        courseOffering.setDeleted(true);
        courseOfferingRepository.save(
                courseOffering
        );
    }

    private CourseOffering findCourseOffering(
            UUID id
    ) {
        return courseOfferingRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course offering not found"
                        )
                );
    }

    private void validateDateRange(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate
    ) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be greater than or equal to start date"
            );
        }
    }

    private void normalize(
            CourseOffering courseOffering
    ) {
        courseOffering.setAcademicYear(
                courseOffering
                        .getAcademicYear()
                        .trim()
        );
        courseOffering.setSemester(
                courseOffering
                        .getSemester()
                        .trim()
                        .toUpperCase()
        );
        courseOffering.setSection(
                courseOffering
                        .getSection()
                        .trim()
                        .toUpperCase()
        );
        if (courseOffering.getRoom() != null) {
            courseOffering.setRoom(
                    courseOffering
                            .getRoom()
                            .trim()
            );
        }
    }
}