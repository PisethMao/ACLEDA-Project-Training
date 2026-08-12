package com.acleda.training.studentmanagement.features.course.offering;

import com.acleda.training.studentmanagement.features.course.offering.dto.CourseOfferingResponse;
import com.acleda.training.studentmanagement.features.course.offering.dto.CreateCourseOfferingRequest;
import com.acleda.training.studentmanagement.features.course.offering.dto.UpdateCourseOfferingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CourseOfferingService {
    CourseOfferingResponse createCourseOffering(CreateCourseOfferingRequest request);

    CourseOfferingResponse getCourseOfferingById(UUID courseOfferingId);

    Page<CourseOfferingResponse> getCourseOfferings(
            String keyword,
            UUID courseId,
            UUID instructorId,
            String academicYear,
            String semester,
            Pageable pageable
    );

    CourseOfferingResponse updateCourseOffering(
            UUID courseOfferingId,
            UpdateCourseOfferingRequest request
    );

    void deleteCourseOffering(UUID courseOfferingId);
}
