package com.istad.docuhub.feature.studentDetail.service.serviceImpl;

import com.istad.docuhub.Repository.StudentDetailRepository;
import com.istad.docuhub.Repository.UserRepository;
import com.istad.docuhub.domain.StudentDetail;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.studentDetail.dto.StudentDetailRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentDetailResponse;
import com.istad.docuhub.feature.studentDetail.dto.UpdateStudentDetailRequest;
import com.istad.docuhub.feature.studentDetail.mapper.StudentMapper;
import com.istad.docuhub.feature.studentDetail.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentDetailRepository repository;
    private final UserRepository userRepository;
    private final StudentMapper studentMapperImpl;




    @Override
    public StudentDetailResponse create(StudentDetailRequest request) {
        // 1. Validate that the user exists
        User user = userRepository.findByUuid(request.userUuid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        // 2. Build the entity (mapper ignores user)
        StudentDetail student = studentMapperImpl.toEntity(request);
        student.setUuid(UUID.randomUUID().toString());
        student.setUser(user);            // attach managed user

        // 3. Persist
        repository.save(student);
        return studentMapperImpl.toResponse(student);
    }





    @Override
    public List<StudentDetailResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(studentMapperImpl::toResponse)
                .toList();
    }





    @Override
    public StudentDetailResponse getByUuid(String uuid) {
        return repository.findByUuid(uuid)
                .map(studentMapperImpl::toResponse)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student detail not found"));
    }





    @Override
    public StudentDetailResponse updatePartial(String uuid, UpdateStudentDetailRequest request) {
        StudentDetail existing = repository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student detail not found"));

        // if a new userUuid is provided, validate & switch the link
        if (request.userUuid() != null) {
            User user = userRepository.findByUuid(request.userUuid())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "User not found"));
            existing.setUser(user);
        }

        studentMapperImpl.updatePartial(existing, request);
        repository.save(existing);
        return studentMapperImpl.toResponse(existing);
    }





    @Override
    public void delete(String uuid) {
        if (!repository.existsByUuid(uuid)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Student detail not found");
        }
        repository.deleteByUuid(uuid);
    }



}