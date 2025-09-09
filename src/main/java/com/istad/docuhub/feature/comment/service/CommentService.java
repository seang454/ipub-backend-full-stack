package com.istad.docuhub.feature.comment.service;

import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.UpdateCommentRequest;

public interface CommentService {

    CommentResponse createComment(CreateCommentRequest createCommentRequest);
    CommentResponse editComment(UpdateCommentRequest editCommentRequest);
    void deleteCommentByUuid(String userUuid );

}
