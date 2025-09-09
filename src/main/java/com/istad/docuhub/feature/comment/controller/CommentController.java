package com.istad.docuhub.feature.comment.controller;

import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.UpdateCommentRequest;
import com.istad.docuhub.feature.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    // -------------------
    // Create Comment
    // -------------------
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @RequestBody @Valid CreateCommentRequest request) {

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

        // Optional: ensure path UUID and body UUID match
        if (!uuid.equals(request.commentUuid())) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        CommentResponse response = commentService.editComment(request);
        return ResponseEntity.ok(response);
    }



    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> deleteComment(@PathVariable String uuid) {
        commentService.deleteCommentByUuid(uuid);
        return ResponseEntity.ok(Map.of(
                "uuid", uuid,
                "message", "Comment deleted successfully"
        ));
    }

}
