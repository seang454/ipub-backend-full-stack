package com.istad.docuhub.feature.comment.dto;

import jakarta.validation.constraints.NotBlank;


public record CreateCommentRequest(
        @NotBlank(message = "Content is required")
        String content,

        @NotBlank(message = "Paper UUID is required")
        String paperUuid,

        String parentUuid  // Optional: if null, it's a root comment; if provided, it's a reply
) {}
