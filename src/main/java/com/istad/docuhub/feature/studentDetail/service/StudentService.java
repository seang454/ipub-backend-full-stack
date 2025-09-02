package com.istad.docuhub.feature.studentDetail.service;

import com.istad.docuhub.feature.studentDetail.dto.StudentDetailRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentDetailResponse;
import com.istad.docuhub.feature.studentDetail.dto.UpdateStudentDetailRequest;

import java.util.List;

public interface StudentService {

    StudentDetailResponse create(StudentDetailRequest request);

    List<StudentDetailResponse> getAll();

    StudentDetailResponse getByUuid(String uuid);

    StudentDetailResponse updatePartial(String uuid, UpdateStudentDetailRequest request);

    void delete(String uuid);


}
