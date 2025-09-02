package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteCommentRequest(
        @NotNull(message = "User id can not be null...")
        Integer userId,
        @NotNull(message = "Comment id can not be null...")
        Integer commentId
) {
}
