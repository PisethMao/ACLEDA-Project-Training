package com.acleda.training.studentmanagement.features.enrollment;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.course.Course;
import com.acleda.training.studentmanagement.features.course.CourseRepository;
import com.acleda.training.studentmanagement.features.enrollment.dto.CreateEnrollmentRequest;
import com.acleda.training.studentmanagement.features.enrollment.dto.EnrollmentResponse;
import com.acleda.training.studentmanagement.features.enrollment.dto.UpdateEnrollmentRequest;
import com.acleda.training.studentmanagement.features.enrollment.dto.UpdateEnrollmentResultRequest;
import com.acleda.training.studentmanagement.features.student.Student;
import com.acleda.training.studentmanagement.features.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentServiceImpl
        implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentMapper enrollmentMapper;

    @Override
    public EnrollmentResponse createEnrollment(
            CreateEnrollmentRequest request
    ) {
        Student student = studentRepository
                .findByIdAndDeletedFalse(
                        request.studentId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found"
                        )
                );
        Course course = courseRepository
                .findByIdAndDeletedFalse(
                        request.courseId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Course not found"
                        )
                );
        boolean exists =
                enrollmentRepository
                        .existsByStudent_IdAndCourse_IdAndSemesterAndAcademicYearAndDeletedFalse(
                                request.studentId(),
                                request.courseId(),
                                request.semester(),
                                request.academicYear()
                        );
        if (exists) {
            throw new ConflictException(
                    "Student is already enrolled in this course for "
                            + request.semester()
                            + " "
                            + request.academicYear()
            );
        }
        Enrollment enrollment =
                Enrollment.builder()
                        .student(student)
                        .course(course)
                        .semester(request.semester())
                        .academicYear(
                                request.academicYear()
                                        .trim()
                        )
                        .enrollmentDate(
                                request.enrollmentDate() != null
                                        ? request.enrollmentDate()
                                        : LocalDate.now()
                        )
                        .status(
                                EnrollmentStatus.ENROLLED
                        )
                        .deleted(false)
                        .build();
        Enrollment savedEnrollment =
                enrollmentRepository.saveAndFlush(
                        enrollment
                );
        return enrollmentMapper.toResponse(
                savedEnrollment
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnrollmentResponse> getEnrollments(
            String keyword,
            UUID studentId,
            UUID courseId,
            Semester semester,
            String academicYear,
            EnrollmentStatus status,
            Pageable pageable
    ) {
        return enrollmentRepository
                .searchEnrollments(
                        keyword,
                        studentId,
                        courseId,
                        semester,
                        academicYear,
                        status,
                        pageable
                )
                .map(
                        enrollmentMapper::toResponse
                );
    }

    private Enrollment findEnrollmentById(
            UUID enrollmentId
    ) {
        return enrollmentRepository
                .findByIdAndDeletedFalse(
                        enrollmentId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Enrollment not found"
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(
            UUID enrollmentId
    ) {
        Enrollment enrollment =
                findEnrollmentById(
                        enrollmentId
                );
        return enrollmentMapper.toResponse(
                enrollment
        );
    }

    @Override
    public EnrollmentResponse updateEnrollment(
            UUID enrollmentId,
            UpdateEnrollmentRequest request
    ) {
        Enrollment enrollment =
                findEnrollmentById(
                        enrollmentId
                );
        Student student =
                studentRepository
                        .findByIdAndDeletedFalse(
                                request.studentId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Student not found"
                                )
                        );
        Course course =
                courseRepository
                        .findByIdAndDeletedFalse(
                                request.courseId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Course not found"
                                )
                        );
        boolean duplicate =
                enrollmentRepository
                        .existsByStudent_IdAndCourse_IdAndSemesterAndAcademicYearAndDeletedFalseAndIdNot(
                                request.studentId(),
                                request.courseId(),
                                request.semester(),
                                request.academicYear(),
                                enrollmentId
                        );
        if (duplicate) {
            throw new ConflictException(
                    "Student is already enrolled in this course for "
                            + request.semester()
                            + " "
                            + request.academicYear()
            );
        }
        boolean noChanges =
                enrollment.getStudent()
                        .getId()
                        .equals(request.studentId())
                        &&
                        enrollment.getCourse()
                                .getId()
                                .equals(request.courseId())
                        &&
                        enrollment.getSemester()
                                == request.semester()
                        &&
                        enrollment.getAcademicYear()
                                .equals(request.academicYear())
                        &&
                        enrollment.getEnrollmentDate()
                                .equals(request.enrollmentDate())
                        &&
                        enrollment.getStatus()
                                == request.status();
        if (noChanges) {
            return enrollmentMapper.toResponse(
                    enrollment
            );
        }
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setSemester(
                request.semester()
        );
        enrollment.setAcademicYear(
                request.academicYear()
                        .trim()
        );
        enrollment.setEnrollmentDate(
                request.enrollmentDate()
        );
        enrollment.setStatus(
                request.status()
        );
        Enrollment updatedEnrollment =
                enrollmentRepository.save(
                        enrollment
                );
        return enrollmentMapper.toResponse(
                updatedEnrollment
        );
    }

    @Override
    public EnrollmentResponse updateEnrollmentResult(
            UUID enrollmentId,
            UpdateEnrollmentResultRequest request
    ) {
        Enrollment enrollment =
                findEnrollmentById(
                        enrollmentId
                );
        enrollment.setScore(
                request.score()
        );
        enrollment.setGrade(
                request.grade() == null
                        ? null
                        : request.grade().trim()
        );
        enrollment.setStatus(
                request.status()
        );
        Enrollment updatedEnrollment =
                enrollmentRepository.save(
                        enrollment
                );
        return enrollmentMapper.toResponse(
                updatedEnrollment
        );
    }

    @Override
    public void deleteEnrollment(
            UUID enrollmentId
    ) {
        Enrollment enrollment =
                findEnrollmentById(
                        enrollmentId
                );

        enrollment.setDeleted(true);
        enrollmentRepository.save(
                enrollment
        );
    }
}