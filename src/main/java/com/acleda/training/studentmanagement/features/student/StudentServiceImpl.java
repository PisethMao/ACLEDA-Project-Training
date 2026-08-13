package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import com.acleda.training.studentmanagement.features.student.dto.StudentUpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    // Map the same field
//    private void validateDuplicateForCreate(StudentRequest request) {
//        if (studentRepository.existsByStudentCodeIgnoreCase(
//                request.studentCode()
//        )) {
//            throw new ConflictException(
//                    "Student code '" + request.studentCode() + "' already exists"
//            );
//        }
//        if (studentRepository.existsByEmailIgnoreCase(
//                request.email()
//        )) {
//            throw new ConflictException(
//                    "Email '" + request.email() + "' already exists"
//            );
//        }
//    }

    // Map difference field follow request dto field
    private void validateDuplicateForCreate(
            StudentRequest request
    ) {
        if (studentRepository.existsByStudentCodeIgnoreCase(
                request.code().trim()
        )) {
            throw new ConflictException(
                    "Student code '"
                            + request.code()
                            + "' already exists"
            );
        }
        if (studentRepository.existsByEmailIgnoreCase(
                request.emailAddress().trim()
        )) {
            throw new ConflictException(
                    "Email '"
                            + request.emailAddress()
                            + "' already exists"
            );
        }
    }

    @Override
    @Transactional
    @CachePut(
            value = "students",
            key = "#result.id()"
    )
    public StudentResponse createStudent(StudentRequest request) {
        validateDuplicateForCreate(request);
        Student student = studentMapper.toEntity(request);
        student.setStatus(StudentStatus.ACTIVE);
        student.setDeleted(false);
        Student savedStudent = studentRepository.saveAndFlush(student);
        return studentMapper.toResponse(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StudentResponse> getStudents(Pageable pageable) {
        return studentRepository
                .findAllByDeletedFalse(pageable)
                .map(studentMapper::toResponse);
    }

    private Student findStudent(UUID studentId) {
        return studentRepository.findByIdAndDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Student with ID " + studentId + " was not found"
                ));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = "students",
            key = "#studentId"
    )
    public StudentResponse getStudentById(UUID studentId) {
        Student student = findStudent(studentId);
        return studentMapper.toResponse(student);
    }

    // Map the same field
//    private void validateDuplicateForUpdate(
//            UUID studentId,
//            StudentRequest request
//    ) {
//        if (studentRepository.existsByStudentCodeIgnoreCaseAndIdNot(
//                request.studentCode(),
//                studentId
//        )) {
//            throw new ConflictException(
//                    "Student code '" + request.studentCode() + "' already exists"
//            );
//        }
//        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(
//                request.email(),
//                studentId
//        )) {
//            throw new ConflictException(
//                    "Email '" + request.email() + "' already exists"
//            );
//        }
//    }

    // Map difference field follow request dto field
    private void validateDuplicateForUpdate(
            UUID studentId,
            StudentRequest request
    ) {
        if (studentRepository
                .existsByStudentCodeIgnoreCaseAndIdNot(
                        request.code().trim(),
                        studentId
                )) {
            throw new ConflictException(
                    "Student code '"
                            + request.code()
                            + "' already exists"
            );
        }
        if (studentRepository
                .existsByEmailIgnoreCaseAndIdNot(
                        request.emailAddress().trim(),
                        studentId
                )) {
            throw new ConflictException(
                    "Email '"
                            + request.emailAddress()
                            + "' already exists"
            );
        }
    }

    @Override
    @Transactional
    @CachePut(
            value = "students",
            key = "#studentId"
    )
    public StudentUpdateResult updateStudent(
            UUID studentId,
            StudentRequest request
    ) {
        Student student = findStudent(studentId);
        // Map the same field
//        boolean noChanges =
//                Objects.equals(
//                        student.getStudentCode(),
//                        request.studentCode()
//                )
//                        && Objects.equals(
//                        student.getFirstName(),
//                        request.firstName()
//                )
//                        && Objects.equals(
//                        student.getLastName(),
//                        request.lastName()
//                )
//                        && Objects.equals(
//                        student.getGender(),
//                        request.gender()
//                )
//                        && Objects.equals(
//                        student.getDateOfBirth(),
//                        request.dateOfBirth()
//                )
//                        && Objects.equals(
//                        student.getEmail(),
//                        request.email()
//                )
//                        && Objects.equals(
//                        student.getPhoneNumber(),
//                        request.phoneNumber()
//                )
//                        && Objects.equals(
//                        student.getAddress(),
//                        request.address()
//                );

        // Map difference field follow request dto field
        boolean noChanges =
                Objects.equals(
                        student.getStudentCode(),
                        request.code()
                )
                        && Objects.equals(
                        student.getFirstName(),
                        request.givenName()
                )
                        && Objects.equals(
                        student.getLastName(),
                        request.familyName()
                )
                        && Objects.equals(
                        student.getGender(),
                        request.sex()
                )
                        && Objects.equals(
                        student.getDateOfBirth(),
                        request.birthDate()
                )
                        && Objects.equals(
                        student.getEmail(),
                        request.emailAddress()
                )
                        && Objects.equals(
                        student.getPhoneNumber(),
                        request.phone()
                )
                        && Objects.equals(
                        student.getAddress(),
                        request.homeAddress()
                )
                        && Objects.equals(
                        student.getEnrolledAt(),
                        request.enrollmentDate()
                );
        if (noChanges) {
            return new StudentUpdateResult(
                    studentMapper.toResponse(student),
                    false
            );
        }
        validateDuplicateForUpdate(
                studentId,
                request
        );
        studentMapper.updateEntity(
                request,
                student
        );
        Student updatedStudent =
                studentRepository.saveAndFlush(student);
        return new StudentUpdateResult(
                studentMapper.toResponse(updatedStudent),
                true
        );
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "students",
            key = "#studentId"
    )
    public void deleteStudent(UUID studentId) {
        Student student = findStudent(studentId);
        student.setDeleted(true);
        student.setStatus(StudentStatus.INACTIVE);
        studentRepository.save(student);
    }
}