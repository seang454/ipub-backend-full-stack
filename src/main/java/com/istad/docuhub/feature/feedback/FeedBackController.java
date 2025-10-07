package com.istad.docuhub.feature.feedback;


import com.istad.docuhub.feature.feedback.dto.FeedBackUpdate;
import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<FeedbackResponse> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,   // page index (0 = first page)
            @RequestParam(defaultValue = "10") int size,  // default 10 items per page
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return feedbackService.getAllFeedBack(pageable);
    }

    @PutMapping("/{paperUuid}")
    public ResponseEntity<?> updateFeedbackStatus(@PathVariable String paperUuid, @RequestBody FeedBackUpdate feedBackUpdate) {
        feedbackService.updateFeedbackStatus(paperUuid, feedBackUpdate);
        return new ResponseEntity<>(
                Map.of(
                        "message", "Update feedback status successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/{paperUuid}")
    public ResponseEntity<?> getFeedbackByPaperUuid(@PathVariable String paperUuid){
        return ResponseEntity.ok(feedbackService.getFeedbackByPaperUuid(paperUuid));
    }

    @GetMapping("/author")
    public ResponseEntity<?> getAllFeedbackByAuthor(){
        return ResponseEntity.ok(feedbackService.getAllFeedbackByAuthor());
    }
}
