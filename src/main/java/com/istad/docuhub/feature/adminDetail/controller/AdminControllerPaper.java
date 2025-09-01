package com.istad.docuhub.feature.adminDetail.controller;

import com.istad.docuhub.feature.paper.PaperService;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminControllerPaper {

    private final PaperService paperService;

    @GetMapping("/papers")
    public List<PaperResponse> getAllPapers() {
        return paperService.getAllPapers();
    }
}

