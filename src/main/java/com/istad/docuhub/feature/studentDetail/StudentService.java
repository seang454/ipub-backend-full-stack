package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.feature.studentDetail.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentService {
    void createStudentDetail(StudentRequest studentRequest);
    StudentResponse approveStudentDetail(StudentApproveRequest approvRequest);
    void rejectStudentDetail(RejectStudentRequest rejectRequest);
    StudentResponse findStudentDetailByUserUuid(String userUuid);
    Page<StudentResponse> findStudentPendingStudents(int page, int size);
    StudentResponse updateStudentDetailByUserUuid(String userUuid, UpdateStudentRequest updateRequest);
}
