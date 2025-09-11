package com.istad.docuhub.feature.comment;

import com.istad.docuhub.feature.comment.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    // -------------------
    // Create Comment (can be root comment or reply)
    // -------------------
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody @Valid CreateCommentRequest request) {

        log.info("Creating new comment for paper: {}", request.paperUuid());
        if (request.parentUuid() != null) {
            log.info("This is a reply to comment: {}", request.parentUuid());
        }

        CommentResponse response = commentService.createComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // -------------------
    // Edit Comment
    // -------------------
    @PutMapping("/{uuid}")
    public ResponseEntity<CommentResponse> editComment(
            @PathVariable("uuid") String uuid,
            @RequestBody @Valid UpdateCommentRequest request) {

        log.info("Editing comment: {}", uuid);

        // Ensure path UUID and body UUID match
        if (!uuid.equals(request.commentUuid())) {
            log.warn("Path UUID {} doesn't match body UUID {}", uuid, request.commentUuid());
            return ResponseEntity.badRequest().body(null);
        }

        CommentResponse response = commentService.editComment(request);
        return ResponseEntity.ok(response);
    }

    // -------------------
    // Delete Comment (Soft Delete)
    // -------------------
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteComment(@PathVariable String uuid) {
        log.info("Deleting comment: {}", uuid);

        commentService.deleteCommentByUuid(uuid);
        return ResponseEntity.ok(Map.of(
                "uuid", uuid,
                "message", "Comment deleted successfully"
        ));
    }

    // -------------------
    // Get Single Comment by UUID
    // -------------------
    @GetMapping("/{uuid}")
    public ResponseEntity<CommentResponse> getComment(@PathVariable String uuid) {
        log.info("Fetching comment: {}", uuid);

        CommentResponse response = commentService.getCommentByUuid(uuid);
        return ResponseEntity.ok(response);
    }

    // -------------------
    // Get All Comments for a Paper (with reply hierarchy)
    // -------------------
    @GetMapping("/paper/{paperUuid}")
    public ResponseEntity<CommentTreeResponse> getPaperComments(
            @PathVariable String paperUuid) {

        log.info("Fetching all comments for paper: {}", paperUuid);

        CommentTreeResponse response = commentService.getCommentsForPaper(paperUuid);
        return ResponseEntity.ok(response);
    }

    // -------------------
    // Get Replies for a Specific Comment
    // -------------------
    @GetMapping("/{uuid}/replies")
    public ResponseEntity<?> getCommentReplies(@PathVariable String uuid) {
        log.info("Fetching replies for comment: {}", uuid);

        // This would require adding a new method to your service
        // For now, you can get the comment with its replies using getComment
        CommentResponse response = commentService.getCommentByUuid(uuid);
        return ResponseEntity.ok(Map.of(
                "parentComment", response,
                "replies", response.replies()
        ));
    }
}