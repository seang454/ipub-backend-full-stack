package com.istad.docuhub.feature.star.dto;

import jakarta.validation.constraints.NotNull;

public record DeleteStarRequest(
        @NotNull(message = "User ID is required")
        Integer userId,

        @NotNull(message = "Paper ID is required")
        Integer paperId
) {
}
