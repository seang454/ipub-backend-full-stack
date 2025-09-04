package com.istad.docuhub.feature.adviserAssignment;


import com.istad.docuhub.feature.adviserAssignment.dto.*;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/paper")
public class AdviserAssignmentController {
    private final AdviserAssignmentServiceImpl adviserAssignmentService;
    @PostMapping("/assign-adviser")
    public ResponseEntity<AdviserAssignmentResponse> assignAdviser(
            @RequestBody AdviserAssignmentRequest request
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.assignAdviserToPaper(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reassign-adviser")
    public ResponseEntity<AdviserAssignmentResponse> reassignAdviser(
            @RequestBody ReassignAdviserRequest request
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.reassignAdviser(
                request.paperUuid(),
                request.newAdviserUuid(),
                request.adminUuid(),
                request.deadline()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reject")
    public ResponseEntity<PaperResponse> rejectPaper(@RequestBody RejectPaperRequest rejectRequest) {
        PaperResponse response = adviserAssignmentService.rejectPaperByAdmin(rejectRequest);
        return ResponseEntity.ok(response);
    }

    // Review paper by adviser
    @PostMapping("/adviser-review")
    public ResponseEntity<AdviserAssignmentResponse> reviewPaper(
            @RequestBody AdviserReviewRequest reviewRequest
    ) {
        AdviserAssignmentResponse response = adviserAssignmentService.reviewPaperByAdviser(reviewRequest);
        return ResponseEntity.ok(response);
    }

    // Get all assignments of a specific adviser
    @GetMapping("/adviser/{adviserUuid}")
    public ResponseEntity<?> getAssignmentsByAdviser(@PathVariable String adviserUuid) {
        return ResponseEntity.ok(
                adviserAssignmentService.getAssignmentsByAdviserUuid(adviserUuid)
        );
    }

}
