package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(

        @NotBlank(message = "Content is required")
        String content,

        @NotNull(message = "Paper ID is required")
        Integer paperId

) {
}
