package com.istad.docuhub.feature.studentDetail;

import com.istad.docuhub.feature.studentDetail.dto.StudentLogic;
import com.istad.docuhub.feature.studentDetail.dto.StudentRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentResponse;
import com.istad.docuhub.feature.studentDetail.dto.UpdateStudentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-promote")
public class StudentDetailController {

    private final StudentService studentService;

    @PostMapping("/create-student-detail")
    public ResponseEntity<?> createStudentDetail(@RequestBody StudentRequest studentRequest) {
        studentService.createStudentDetail(studentRequest);
        return new ResponseEntity<>(
                "Create student detail successfully",
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/student/{userUuid}")
    public ResponseEntity<StudentResponse> updateStudentDetail(
            @PathVariable String userUuid,
            @Valid @RequestBody UpdateStudentRequest updateRequest
    ) {
        StudentResponse updatedStudent = studentService.updateStudentDetailByUserUuid(userUuid, updateRequest);
        return ResponseEntity.ok(updatedStudent);
    }


    // find studentdetail by uuid
    @GetMapping("/student/{userUuid}")
    public StudentResponse findStudentDetailByUserUuid(@PathVariable String userUuid) {
        return studentService.findStudentDetailApprovedByUserUuid(userUuid);
    }


    // find studentdetail by uuid
    @GetMapping("/pending/student/{userUuid}")
    public StudentLogic findStudentDetailPendingByUserUuid(@PathVariable String userUuid) {
        return studentService.findStudentDetailPendingByUserUuid(userUuid);
    }

}
