package com.istad.docuhub.feature.adminDetail.controller;

import com.istad.docuhub.feature.adminDetail.AdminService;
import com.istad.docuhub.feature.paper.PaperService;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminControllerPaper {

    private final PaperService paperService;
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/papers")
    public List<PaperResponse> getAllPapers() {
        return paperService.getAllPapers();
    }

    @PostMapping("/create-student")
    public ResponseEntity<String> createStudent(@RequestBody UserCreateDto dto) {
        adminService.createStudent(dto);
        return ResponseEntity.ok("Student created successfully");
    }

    @PostMapping("/create-adviser")
    public ResponseEntity<String> createAdviser(@RequestBody UserCreateDto dto) {
        adminService.createAdviser(dto);
        return ResponseEntity.ok("Adviser created successfully");
    }

}

