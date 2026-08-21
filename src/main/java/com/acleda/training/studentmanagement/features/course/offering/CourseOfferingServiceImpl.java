package com.acleda.training.studentmanagement.features.course.offering;

import com.acleda.training.studentmanagement.config.CacheProperties;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseOfferingServiceImpl
        implements CourseOfferingService {
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final CourseOfferingMapper courseOfferingMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    @Transactional
    public CourseOfferingResponse createCourseOffering(
            CreateCourseOfferingRequest request
    ) {
        validateDateRange(
                request.startDate(),
                request.endDate()
        );
        Course course =
                courseRepository
                        .findByIdAndDeletedFalse(
                                request.courseId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Course not found"
                                        )
                        );
        Instructor instructor =
                instructorRepository
                        .findByIdAndDeletedFalse(
                                request.instructorId()
                        )
                        .orElseThrow(
                                () ->
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
                courseOfferingRepository
                        .saveAndFlush(courseOffering);
        return courseOfferingMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseOfferingResponse getCourseOfferingById(
            UUID courseOfferingId
    ) {
        CourseOfferingResponse cachedOffering =
                getCourseOfferingFromCache(
                        courseOfferingId
                );
        if (cachedOffering != null) {
            return cachedOffering;
        }
        CourseOffering courseOffering =
                findCourseOffering(
                        courseOfferingId
                );
        CourseOfferingResponse response =
                courseOfferingMapper.toResponse(
                        courseOffering
                );
        cacheCourseOffering(
                courseOfferingId,
                response
        );
        return response;
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

    @Override
    @Transactional
    public CourseOfferingResponse updateCourseOffering(
            UUID courseOfferingId,
            UpdateCourseOfferingRequest request
    ) {
        CourseOffering courseOffering =
                findCourseOffering(
                        courseOfferingId
                );
        validateDateRange(
                request.startDate(),
                request.endDate()
        );
        Course course =
                courseRepository
                        .findByIdAndDeletedFalse(
                                request.courseId()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Course not found"
                                        )
                        );
        Instructor instructor =
                instructorRepository
                        .findByIdAndDeletedFalse(
                                request.instructorId()
                        )
                        .orElseThrow(
                                () ->
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
                courseOfferingRepository.save(
                        courseOffering
                );
        evictCourseOfferingCache(
                courseOfferingId
        );
        return courseOfferingMapper.toResponse(
                saved
        );
    }

    @Override
    @Transactional
    public void deleteCourseOffering(
            UUID courseOfferingId
    ) {
        CourseOffering courseOffering =
                findCourseOffering(
                        courseOfferingId
                );
        courseOffering.setDeleted(true);
        courseOfferingRepository.save(
                courseOffering
        );
        evictCourseOfferingCache(
                courseOfferingId
        );
    }

    private CourseOfferingResponse getCourseOfferingFromCache(
            UUID courseOfferingId
    ) {
        String key =
                buildCacheKey(
                        courseOfferingId
                );
        try {
            String json =
                    redisTemplate
                            .opsForValue()
                            .get(key);
            if (json == null) {
                log.info(
                        "CourseOffering cache MISS - key={}",
                        key
                );
                return null;
            }
            CourseOfferingResponse response =
                    objectMapper.readValue(
                            json,
                            CourseOfferingResponse.class
                    );
            log.info(
                    "CourseOffering cache HIT - key={}",
                    key
            );
            return response;
        } catch (JacksonException e) {
            log.warn(
                    "Invalid CourseOffering cache value - key={}",
                    key,
                    e
            );
            evictCourseOfferingCache(
                    courseOfferingId
            );
            return null;
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while reading CourseOffering - key={}",
                    key,
                    e
            );
            return null;
        }
    }

    private void cacheCourseOffering(
            UUID courseOfferingId,
            CourseOfferingResponse response
    ) {
        String key =
                buildCacheKey(
                        courseOfferingId
                );
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
                                    .courseOffering()
                                    .ttl()
                    );
            log.info(
                    "CourseOffering cache SET - key={}, ttl={}",
                    key,
                    cacheProperties
                            .courseOffering()
                            .ttl()
            );
        } catch (JacksonException e) {
            log.warn(
                    "Could not serialize CourseOffering for Redis - id={}",
                    courseOfferingId,
                    e
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while caching CourseOffering - key={}",
                    key,
                    e
            );
        }
    }

    private void evictCourseOfferingCache(
            UUID courseOfferingId
    ) {
        String key =
                buildCacheKey(
                        courseOfferingId
                );
        try {
            Boolean deleted =
                    redisTemplate.delete(key);
            log.info(
                    "CourseOffering cache DELETE - key={}, deleted={}",
                    key,
                    deleted
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while deleting CourseOffering cache - key={}",
                    key,
                    e
            );
        }
    }

    private String buildCacheKey(
            UUID courseOfferingId
    ) {
        return cacheProperties
                .courseOffering()
                .keyPrefix()
                + courseOfferingId;
    }

    private CourseOffering findCourseOffering(
            UUID id
    ) {
        return courseOfferingRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Course offering not found"
                                )
                );
    }

    private void validateDateRange(
            LocalDate startDate,
            LocalDate endDate
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