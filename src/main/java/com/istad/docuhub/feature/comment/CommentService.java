package com.istad.docuhub.feature.comment;

import com.istad.docuhub.feature.comment.dto.*;

public interface CommentService {
    CommentResponse createComment(CreateCommentRequest createCommentRequest);
    CommentResponse editComment(UpdateCommentRequest editCommentRequest);
    void deleteCommentByUuid(String commentUuid);
    CommentResponse getCommentByUuid(String commentUuid);
    CommentTreeResponse getCommentsForPaper(String paperUuid);
}