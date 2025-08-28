package com.istad.docuhub.feature.studentDetail.controller;


import com.istad.docuhub.feature.studentDetail.dto.StudentDetailRequest;
import com.istad.docuhub.feature.studentDetail.dto.StudentDetailResponse;
import com.istad.docuhub.feature.studentDetail.dto.UpdateStudentDetailRequest;
import com.istad.docuhub.feature.studentDetail.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student-detail")
@RequiredArgsConstructor
public class StudentDetailController {

    private final StudentService service;


    @PostMapping
    public StudentDetailResponse create(@RequestBody StudentDetailRequest request) {
        return service.create(request);
    }


    @GetMapping
    public List<StudentDetailResponse> getAll() {
        return service.getAll();
    }



    @GetMapping("/{uuid}")
    public StudentDetailResponse getByUuid(@PathVariable String uuid) {
        return service.getByUuid(uuid);
    }



    @PatchMapping("/{uuid}")
    public StudentDetailResponse updatePartial(
            @PathVariable String uuid,
            @RequestBody UpdateStudentDetailRequest request
    ) {
        return service.updatePartial(uuid, request);
    }



    @DeleteMapping("/{uuid}")
    public void delete(@PathVariable String uuid) {
        service.delete(uuid);
    }


}




















