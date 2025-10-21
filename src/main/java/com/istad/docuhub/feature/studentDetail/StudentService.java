package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.feature.studentDetail.dto.*;
import com.istad.docuhub.feature.user.dto.UserPublicResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    void createStudentDetail(StudentRequest studentRequest);
    void rejectStudentDetail(RejectStudentRequest rejectRequest);
    StudentResponse findStudentDetailByUserUuid(String userUuid);
    Page<StudentResponse> findStudentPendingStudents(int page, int size);
    StudentResponse updateStudentDetailByUserUuid(String userUuid, UpdateStudentRequest updateRequest);
    List<UserPublicResponse> getAllStudentAdvisers();
    StudentResponse findStudentDetailApprovedByUserUuid(String userUuid);
    StudentLogic findStudentDetailPendingByUserUuid(String userUuid);
}
