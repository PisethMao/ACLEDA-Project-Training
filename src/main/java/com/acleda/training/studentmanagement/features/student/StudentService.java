package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import com.acleda.training.studentmanagement.features.student.dto.StudentUpdateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface StudentService {
    StudentResponse createStudent(StudentRequest request);

    Page<StudentResponse> getStudents(Pageable pageable);

    StudentResponse getStudentById(UUID studentId);

    StudentUpdateResult updateStudent(
            UUID studentId,
            StudentRequest request
    );

    void deleteStudent(UUID studentId);
}