package com.istad.docuhub.feature.studentDetail;


import com.istad.docuhub.feature.studentDetail.dto.StudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
public class StudentDetailController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<?> createStudentDetail(@RequestBody StudentRequest studentRequest) {
        studentService.createStudentDetail(studentRequest);
        return new ResponseEntity<>(
                "Create student detail successfully",
                HttpStatus.CREATED
        );
    }
}
