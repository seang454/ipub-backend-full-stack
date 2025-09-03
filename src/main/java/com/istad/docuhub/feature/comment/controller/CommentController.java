package com.istad.docuhub.feature.comment.controller;

import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;
import com.istad.docuhub.feature.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentResponse comment(@Valid @RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.comment(createCommentRequest);
    }


//    @PutMapping("/edit")
//    public CommentResponse editComment(@Valid @RequestBody EditCommentRequest editCommentRequest) {
//        return commentService.editComment(editCommentRequest);
//    }

    @PutMapping   // /api/v1/comments
    public CommentResponse editComment(@Valid @RequestBody EditCommentRequest req) {
        return commentService.editComment(req);
    }



    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@Valid @RequestBody DeleteCommentRequest deleteCommentRequest) {
        commentService.deleteCommentByUserIdAndCommentId(deleteCommentRequest);
    }



}
