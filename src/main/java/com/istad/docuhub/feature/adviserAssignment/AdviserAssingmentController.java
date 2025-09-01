package com.istad.docuhub.feature.adviserAssignment;


import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserReviewRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/adviser-assignments")
public class AdviserAssingmentController {
    private final AdviserAssignmentServiceImpl adviserAssignmentService;
    @PostMapping
    public ResponseEntity<AdviserAssignmentResponse> assignAdviser(
            @RequestBody AdviserAssignmentRequest request
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.assignAdviserToPaper(request);
        return ResponseEntity.ok(response);
    }

    // Review paper by adviser
    @PostMapping("/review")
    public ResponseEntity<AdviserAssignmentResponse> reviewPaper(
            @RequestBody AdviserReviewRequest reviewRequest
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.reviewPaperByAdviser(reviewRequest);
        return ResponseEntity.ok(response);
    }
}
