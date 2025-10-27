package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.domain.*;
import com.istad.docuhub.enums.STATUS;
import com.istad.docuhub.feature.adviserAssignment.AdviserAssignmentRepository;
import com.istad.docuhub.feature.adviserDetail.AdviserDetailRepository;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.sendMail.SendMailService;
import com.istad.docuhub.feature.studentDetail.dto.*;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import com.istad.docuhub.feature.user.dto.UserPublicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentDetailRepository studentDetailRepository;
    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final UserService userService;
    private final AdviserDetailRepository adviserDetailRepository;
    private final AdviserAssignmentRepository adviserAssignmentRepository;
    private final PaperRepository paperRepository;

    int status (STATUS status){
        int statusCode = switch (status) {
            case STATUS.PENDING -> 0;
            case STATUS.APPROVED -> 1;
            case STATUS.ADMIN_REJECTED -> 2; // optional, add more if needed
            default -> -1;
        };
        return statusCode;
    }

    @Override
    public void createStudentDetail(StudentRequest studentRequest) {

        int id;
        int retire = 0;
        do {
            if (retire++ > 10) {
                throw new RuntimeException("Failed to generate unique ID after 10 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (studentDetailRepository.existsById(id));

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
        studentDetail.setStatus(STATUS.PENDING);
        studentDetail.setReason(null);
        studentDetailRepository.save(studentDetail);
    }

    @Override
    public void rejectStudentDetail(RejectStudentRequest rejectRequest) {
        StudentDetail studentDetail = studentDetailRepository.findByUser_Uuid(rejectRequest.userUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student detail not found"));

        // Safely convert string to enum (case-insensitive)
        STATUS status = null;
        for (STATUS s : STATUS.values()) {
            if (s.name().equalsIgnoreCase(rejectRequest.status())) {
                status = s;
                break;
            }
        }

        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status value: " + rejectRequest.status());
        }

        studentDetail.setStatus(status);
        studentDetail.setReason(rejectRequest.reason());
        studentDetail.setIsStudent(false);

        studentDetailRepository.save(studentDetail);
    }



    @Override
    public StudentResponse findStudentDetailByUserUuid(String userUuid) {
        StudentDetail detail = studentDetailRepository.findByUser_Uuid(userUuid)
                .stream()
                .filter(d -> Boolean.FALSE.equals(d.getIsStudent()))          // isStudent = false
                .filter(d -> "PENDING".equalsIgnoreCase(String.valueOf(d.getStatus())))       // status = PENDING
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student detail not found"));

        return new StudentResponse(
                detail.getUuid(),
                detail.getStudentCardUrl(),
                detail.getUniversity(),
                detail.getMajor(),
                detail.getYearsOfStudy(),
                detail.getIsStudent(),
                detail.getStatus().toString(),
                detail.getUser().getUuid()
        );
    }

    @Override
    public Page<StudentResponse> findStudentPendingStudents(int page, int size) {
        Page<StudentDetail> pendingStudents = studentDetailRepository.findPendingStudents(PageRequest.of(page, size));

        if (pendingStudents.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No pending students found");
        }

        return pendingStudents.map(detail -> new StudentResponse(
                detail.getUuid(),
                detail.getStudentCardUrl(),
                detail.getUniversity(),
                detail.getMajor(),
                detail.getYearsOfStudy(),
                detail.getIsStudent(),
                detail.getStatus().toString(),
                detail.getUser() != null ? detail.getUser().getUuid() : null
        ));
    }

    @Override
    public StudentResponse updateStudentDetailByUserUuid(String userUuid, UpdateStudentRequest updateRequest) {
        StudentDetail studentDetail = studentDetailRepository.findByUser_Uuid(userUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student detail not found"));
        studentDetail.setStatus(STATUS.PENDING);
        studentDetail.setReason(null);
        // Update only fields that are not null or empty
        if (updateRequest.studentCardUrl() != null && !updateRequest.studentCardUrl().isEmpty()) {
            studentDetail.setStudentCardUrl(updateRequest.studentCardUrl());
        }
        if (updateRequest.university() != null && !updateRequest.university().isEmpty()) {
            studentDetail.setUniversity(updateRequest.university());
        }
        if (updateRequest.major() != null && !updateRequest.major().isEmpty()) {
            studentDetail.setMajor(updateRequest.major());
        }
        if (updateRequest.yearsOfStudy() != null && !updateRequest.yearsOfStudy().isEmpty()) {
            try {
                studentDetail.setYearsOfStudy(Integer.parseInt(updateRequest.yearsOfStudy()));
            } catch (NumberFormatException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Years of study must be a number");
            }
        }

        studentDetailRepository.save(studentDetail);

        return new StudentResponse(
                studentDetail.getUuid(),
                studentDetail.getStudentCardUrl(),
                studentDetail.getUniversity(),
                studentDetail.getMajor(),
                studentDetail.getYearsOfStudy(),
                studentDetail.getIsStudent(),
                studentDetail.getStatus().toString(),
                studentDetail.getUser().getUuid()
        );
    }

    @Override
    public List<UserPublicResponse> getAllStudentAdvisers() {
        CurrentUser subId = userService.getCurrentUserSub();
        User user = userRepository.findByUuid(subId.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Paper> papers = paperRepository.findPaperByAuthor_Uuid(user.getUuid());
        List<AdviserAssignment> adviserAssignments = adviserAssignmentRepository.findByPaper_UuidIn(papers.stream().map(Paper::getUuid).toList());
        List<String> adviserUuids = adviserAssignments.stream().map(AdviserAssignment::getAdvisor).map(User::getUuid).toList();
        List<User> advisers = userRepository.findByUuidIn(adviserUuids);
        return advisers.stream().map(adviser -> new UserPublicResponse(
                        adviser.getUuid(),
                        adviser.getSlug(),
                        adviser.getGender(),
                        adviser.getFullName(),
                        adviser.getImageUrl(),
                        adviser.getStatus(),
                        adviser.getCreateDate(),
                        adviser.getUpdateDate(),
                        adviser.getBio(),
                        adviser.getIsUser(),
                        adviser.getIsAdmin(),
                        adviser.getIsAdvisor(),
                        adviser.getIsStudent()
                )).toList();
    }

    @Override
    public StudentResponse findStudentDetailApprovedByUserUuid(String userUuid) {
        StudentDetail detail = studentDetailRepository.findByUser_Uuid(userUuid)
                .stream()
                .filter(d -> Boolean.TRUE.equals(d.getIsStudent()))          // isStudent = false
                .filter(d -> "APPROVED".equalsIgnoreCase(String.valueOf(d.getStatus())))       // status = PENDING
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student detail not found"));

        return new StudentResponse(
                detail.getUuid(),
                detail.getStudentCardUrl(),
                detail.getUniversity(),
                detail.getMajor(),
                detail.getYearsOfStudy(),
                detail.getIsStudent(),
                detail.getStatus().toString(),
                detail.getUser().getUuid()
        );
    }

    @Override
    public StudentLogic findStudentDetailPendingByUserUuid(String userUuid) {
        StudentDetail detail = studentDetailRepository.findByUser_Uuid(userUuid)
                .stream()
                .filter(d -> Boolean.FALSE.equals(d.getIsStudent()))
                .filter(d -> d.getStatus() == STATUS.PENDING || d.getStatus() == STATUS.ADMIN_REJECTED)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student detail not found"));

        // Convert enum to string for frontend
        return new StudentLogic(
                detail.getIsStudent(),
                detail.getReason(),
                detail.getStatus().name()
        );
    }

}


