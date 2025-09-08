package com.istad.docuhub.feature.comment;

import com.istad.docuhub.feature.comment.dto.CommentResponse;
import com.istad.docuhub.feature.comment.dto.CreateCommentRequest;
import com.istad.docuhub.feature.comment.dto.DeleteCommentRequest;
import com.istad.docuhub.feature.comment.dto.EditCommentRequest;

import java.util.List;

public interface CommentService {

    // User creates a comment
    CommentResponse createComment(CreateCommentRequest createCommentRequest);

    //User edits a comment
    CommentResponse editComment(EditCommentRequest editCommentRequest);

    // User deletes a comment
    void deleteComment(DeleteCommentRequest deleteCommentRequest);

    // Get number of comments for a specific paper
    long countByPaperId(Integer paperId);

    // Get comments for a specific paper
    List<CommentResponse> getCommentsByPaperId(Integer paperId);

}
