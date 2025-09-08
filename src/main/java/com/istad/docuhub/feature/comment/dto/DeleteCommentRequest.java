package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteCommentRequest(

        @NotNull(message = "Comment id can not be null...")
        Integer commentId
) {
}
