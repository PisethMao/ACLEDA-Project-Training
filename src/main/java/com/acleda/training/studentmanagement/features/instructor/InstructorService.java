package com.acleda.training.studentmanagement.features.instructor;

import com.acleda.training.studentmanagement.features.instructor.dto.CreateInstructorRequest;
import com.acleda.training.studentmanagement.features.instructor.dto.InstructorResponse;
import com.acleda.training.studentmanagement.features.instructor.dto.UpdateInstructorRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface InstructorService {
    InstructorResponse createInstructor(
            CreateInstructorRequest request
    );

    Page<InstructorResponse> getInstructors(
            String keyword,
            UUID departmentId,
            Boolean enabled,
            Pageable pageable
    );

    InstructorResponse getInstructorById(
            UUID instructorId
    );

    InstructorResponse updateInstructor(
            UUID instructorId,
            UpdateInstructorRequest request
    );

    void deleteInstructor(
            UUID instructorId
    );
}
