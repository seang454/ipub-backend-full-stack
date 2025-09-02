package com.istad.docuhub.feature.comment.service;

import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;

public interface CommentService {

    CommentResponse comment(CreateCommentRequest createCommentRequest);

    CommentResponse editComment(EditCommentRequest editCommentRequest);

    void deleteCommentByUserIdAndCommentId(DeleteCommentRequest deleteCommentRequest);

}
