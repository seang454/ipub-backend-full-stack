package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.domain.StudentDetail;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.studentDetail.dto.StudentRequest;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final StudentDetailRepository studentDetailRepository;

    @Override
    public void createStudentDetail(StudentRequest studentRequest) {
        if (studentRequest.studentCardUrl() == null || studentRequest.studentCardUrl().isEmpty()) {
            throw new IllegalArgumentException("Student card URL cannot be null or empty");
        }
        if (studentRequest.university() == null || studentRequest.university().isEmpty()) {
            throw new IllegalArgumentException("University cannot be null or empty");
        }
        if (studentRequest.major() == null || studentRequest.major().isEmpty()) {
            throw new IllegalArgumentException("Major cannot be null or empty");
        }
        if (studentRequest.yearsOfStudy() == null || studentRequest.yearsOfStudy().isEmpty()) {
            throw new IllegalArgumentException("Years of study cannot be null or empty");
        }
        if (studentRequest.userUuid() == null || studentRequest.userUuid().isEmpty()) {
            throw new IllegalArgumentException("User UUID cannot be null or empty");
        }

        int id;
        int retire = 0;
        do {
            if (retire++ > 10) {
                throw new RuntimeException("Failed to generate unique ID after 10 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        }while (studentDetailRepository.existsById(id));

        User user = userRepository.findByUuid(studentRequest.userUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setId(id);
        studentDetail.setUuid(UUID.randomUUID().toString());
        studentDetail.setStudentCardUrl(studentRequest.studentCardUrl());
        studentDetail.setUniversity(studentRequest.university());
        studentDetail.setMajor(studentRequest.major());
        studentDetail.setYearsOfStudy(Integer.parseInt(studentRequest.yearsOfStudy()));
        studentDetail.setUser(user);
        studentDetail.setIsStudent(false);
        studentDetailRepository.save(studentDetail);
    }
}

