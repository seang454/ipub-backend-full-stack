package com.istad.docuhub.feature.feedback;


import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feedback")
public class FeedBackController {

    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> createFeedBack(@RequestBody FeedbackRequest feedbackRequest) {
        feedbackService.createFeedback(feedbackRequest);
        return new ResponseEntity<>(
                Map.of(
                        "message", "Create feedback successfully"
                ), HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<?> getAllFeedBack() {
        return new ResponseEntity<>(
                Map.of(
                        "message", "Get all feedback successfully",
                        "data", feedbackService.getAllFeedBack()
                ), HttpStatus.OK
        );
    }
}
