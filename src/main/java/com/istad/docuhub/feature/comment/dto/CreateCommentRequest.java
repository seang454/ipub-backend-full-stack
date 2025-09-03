package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentRequest(

        @NotNull
        Integer userId,

        @NotNull
        Integer paperId,

        @NotBlank
        String content
) {
}
