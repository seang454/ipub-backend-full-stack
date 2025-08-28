package com.istad.docuhub.feature.star.controller;


import com.istad.docuhub.feature.star.dto.CreateStarRequest;
import com.istad.docuhub.feature.star.dto.DeleteStarRequest;
import com.istad.docuhub.feature.star.dto.StarCountResponse;
import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.star.service.StarService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stars")
@RequiredArgsConstructor
public class StarController {

    private final StarService starService;



    @PostMapping
    public ResponseEntity<StarResponse> starPaper(@Valid @RequestBody CreateStarRequest createStarRequest) {
        StarResponse response = starService.starPaper(createStarRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @DeleteMapping
    public ResponseEntity<Void> unstarPaper(@Valid @RequestBody DeleteStarRequest deleteStarRequest) {
        starService.unstarPaper(deleteStarRequest);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/toggle")
    public ResponseEntity<StarResponse> toggleStar(@Valid @RequestBody CreateStarRequest createStarRequest) {
        StarResponse response = starService.toggleStar(createStarRequest);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/info/{paperId}/{userId}")
    public ResponseEntity<StarCountResponse> getStarInfo(
            @PathVariable Integer paperId,
            @PathVariable Integer userId) {
        StarCountResponse response = starService.getStarInfo(paperId, userId);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/count/{paperId}")
    public ResponseEntity<Long> getStarCount(@PathVariable Integer paperId) {
        StarCountResponse response = starService.getStarInfo(paperId, null);
        return ResponseEntity.ok(response.starCount());
    }



    @GetMapping("/my-stars/{userId}")
    public ResponseEntity<Page<StarResponse>> getUserStarredPapers(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<StarResponse> stars = starService.getUserStarredPapers(userId, page, size);
        return ResponseEntity.ok(stars);
    }



}
