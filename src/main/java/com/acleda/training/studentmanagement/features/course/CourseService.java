package com.acleda.training.studentmanagement.features.course;

import com.acleda.training.studentmanagement.features.course.dto.CourseResponse;
import com.acleda.training.studentmanagement.features.course.dto.CreateCourseRequest;
import com.acleda.training.studentmanagement.features.course.dto.UpdateCourseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseService {
    CourseResponse createCourse(
            CreateCourseRequest request
    );

    Page<CourseResponse> getCourses(
            String keyword,
            UUID departmentId,
            Integer credit,
            Pageable pageable
    );

    CourseResponse getCourseById(
            UUID courseId
    );

    CourseResponse updateCourse(
            UUID courseId,
            UpdateCourseRequest request
    );

    void deleteCourse(
            UUID courseId
    );
}