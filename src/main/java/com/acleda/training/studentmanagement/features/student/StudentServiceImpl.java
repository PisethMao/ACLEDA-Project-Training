package com.acleda.training.studentmanagement.features.student;

import com.acleda.training.studentmanagement.config.CacheProperties;
import com.acleda.training.studentmanagement.exception.ConflictException;
import com.acleda.training.studentmanagement.exception.ResourceNotFoundException;
import com.acleda.training.studentmanagement.features.student.dto.StudentRequest;
import com.acleda.training.studentmanagement.features.student.dto.StudentResponse;
import com.acleda.training.studentmanagement.features.student.dto.StudentUpdateResult;
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

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    private String buildCacheKey(
            UUID studentId
    ) {
        return cacheProperties
                .student()
                .keyPrefix()
                + studentId;
    }

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
    public StudentResponse createStudent(
            StudentRequest request
    ) {
        validateDuplicateForCreate(request);
        Student student =
                studentMapper.toEntity(request);
        student.setStatus(StudentStatus.ACTIVE);
        student.setDeleted(false);
        Student savedStudent =
                studentRepository.saveAndFlush(student);
        return studentMapper.toResponse(
                savedStudent
        );
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

    private StudentResponse getStudentFromCache(
            UUID studentId
    ) {
        String key =
                buildCacheKey(studentId);
        try {
            String json =
                    redisTemplate
                            .opsForValue()
                            .get(key);
            if (json == null) {
                log.info(
                        "Student cache MISS - key={}",
                        key
                );
                return null;
            }
            StudentResponse response =
                    objectMapper.readValue(
                            json,
                            StudentResponse.class
                    );
            log.info(
                    "Student cache HIT - key={}",
                    key
            );
            return response;
        } catch (JacksonException e) {
            log.warn(
                    "Invalid Student cache value - key={}",
                    key,
                    e
            );
            redisTemplate.delete(key);
            return null;
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while reading Student - key={}",
                    key,
                    e
            );
            return null;
        }
    }

    private void cacheStudent(
            UUID studentId,
            StudentResponse response
    ) {
        String key =
                buildCacheKey(studentId);
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
                                    .student()
                                    .ttl()
                    );
            log.info(
                    "Student cache SET - key={}, ttl={}",
                    key,
                    cacheProperties
                            .student()
                            .ttl()
            );
        } catch (JacksonException e) {
            log.warn(
                    "Could not serialize Student for Redis - id={}",
                    studentId,
                    e
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while caching Student - key={}",
                    key,
                    e
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(
            UUID studentId
    ) {
        StudentResponse cachedStudent =
                getStudentFromCache(studentId);
        if (cachedStudent != null) {
            return cachedStudent;
        }
        Student student =
                findStudent(studentId);
        StudentResponse response =
                studentMapper.toResponse(student);
        cacheStudent(
                studentId,
                response
        );
        return response;
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
        evictStudentCache(studentId);
        return new StudentUpdateResult(
                studentMapper.toResponse(updatedStudent),
                true
        );
    }

    private void evictStudentCache(
            UUID studentId
    ) {
        String key =
                buildCacheKey(studentId);
        try {
            Boolean deleted =
                    redisTemplate.delete(key);
            log.info(
                    "Student cache DELETE - key={}, deleted={}",
                    key,
                    deleted
            );
        } catch (DataAccessException e) {
            log.warn(
                    "Redis unavailable while deleting Student cache - key={}",
                    key,
                    e
            );
        }
    }

    @Override
    @Transactional
    public void deleteStudent(
            UUID studentId
    ) {
        Student student =
                findStudent(studentId);
        student.setDeleted(true);
        student.setStatus(
                StudentStatus.INACTIVE
        );
        studentRepository.saveAndFlush(student);
        evictStudentCache(studentId);
    }
}