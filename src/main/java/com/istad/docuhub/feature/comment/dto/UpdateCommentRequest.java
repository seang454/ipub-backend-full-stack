package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateCommentRequest(
        @NotNull(message = "Comment uuid is required")
        String commentUuid,

        @NotBlank(message = "Content cannot be empty")
        String content
) {}
