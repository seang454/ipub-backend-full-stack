package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditCommentRequest(
        @NotNull(message = "Comment id is required...")
        Integer commentId,

        @NotNull(message = "User id is required...")
        Integer userId,

        @NotBlank(message = "Content can not be empty...")
        String content
) {}