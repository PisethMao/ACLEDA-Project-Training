package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import com.acleda.training.studentmanagement.features.student.dto.StudentUpdateResult;
import lombok.RequiredArgsConstructor;
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

    private void validateDuplicateForCreate(StudentRequest request) {
        if (studentRepository.existsByStudentCodeIgnoreCase(
                request.studentCode()
        )) {
            throw new ConflictException(
                    "Student code '" + request.studentCode() + "' already exists"
            );
        }
        if (studentRepository.existsByEmailIgnoreCase(
                request.email()
        )) {
            throw new ConflictException(
                    "Email '" + request.email() + "' already exists"
            );
        }
    }

    @Override
    @Transactional
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
    public StudentResponse getStudentById(UUID studentId) {
        Student student = findStudent(studentId);
        return studentMapper.toResponse(student);
    }

    private void validateDuplicateForUpdate(
            UUID studentId,
            StudentRequest request
    ) {
        if (studentRepository.existsByStudentCodeIgnoreCaseAndIdNot(
                request.studentCode(),
                studentId
        )) {
            throw new ConflictException(
                    "Student code '" + request.studentCode() + "' already exists"
            );
        }
        if (studentRepository.existsByEmailIgnoreCaseAndIdNot(
                request.email(),
                studentId
        )) {
            throw new ConflictException(
                    "Email '" + request.email() + "' already exists"
            );
        }
    }

    @Override
    @Transactional
    public StudentUpdateResult updateStudent(
            UUID studentId,
            StudentRequest request
    ) {
        Student student = findStudent(studentId);
        boolean noChanges =
                Objects.equals(
                        student.getStudentCode(),
                        request.studentCode()
                )
                        && Objects.equals(
                        student.getFirstName(),
                        request.firstName()
                )
                        && Objects.equals(
                        student.getLastName(),
                        request.lastName()
                )
                        && Objects.equals(
                        student.getGender(),
                        request.gender()
                )
                        && Objects.equals(
                        student.getDateOfBirth(),
                        request.dateOfBirth()
                )
                        && Objects.equals(
                        student.getEmail(),
                        request.email()
                )
                        && Objects.equals(
                        student.getPhoneNumber(),
                        request.phoneNumber()
                )
                        && Objects.equals(
                        student.getAddress(),
                        request.address()
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
    public void deleteStudent(UUID studentId) {
        Student student = findStudent(studentId);
        student.setDeleted(true);
        student.setStatus(StudentStatus.INACTIVE);
        studentRepository.save(student);
    }
}