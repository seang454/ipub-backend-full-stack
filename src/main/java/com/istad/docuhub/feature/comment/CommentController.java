package com.istad.docuhub.feature.comment;

import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private CommentService commentService;


    // Create a comment
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CreateCommentRequest request) {
        CommentResponse response = commentService.createComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // Edit a comment
    @PutMapping
    public ResponseEntity<CommentResponse> editComment(@RequestBody EditCommentRequest request) {
        CommentResponse response = commentService.editComment(request);
        return ResponseEntity.ok(response);
    }


    // Delete a comment
    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@RequestBody DeleteCommentRequest request) {
        commentService.deleteComment(request);
        return ResponseEntity.noContent().build(); // HTTP 204
    }


    // Get all comments for a specific paper
    @GetMapping("/paper/{paperId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPaperId(@PathVariable Integer paperId) {
        List<CommentResponse> comments = commentService.getCommentsByPaperId(paperId);
        return ResponseEntity.ok(comments);
    }


    // Get number of comments for a specific paper
    @GetMapping("/paper/{paperId}/count")
    public ResponseEntity<Long> countCommentsByPaperId(@PathVariable Integer paperId) {
        long count = commentService.countByPaperId(paperId);
        return ResponseEntity.ok(count);
    }


}
