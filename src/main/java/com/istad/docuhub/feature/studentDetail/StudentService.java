package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.feature.studentDetail.dto.RejectStudentRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentApproveRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentResponse;

public interface StudentService {
    void createStudentDetail(StudentRequest studentRequest);
    StudentResponse approveStudentDetail(StudentApproveRequest approvRequest);
    void rejectStudentDetail(RejectStudentRequest rejectRequest);
}
