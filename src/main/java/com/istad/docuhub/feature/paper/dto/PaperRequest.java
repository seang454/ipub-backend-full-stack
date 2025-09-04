package com.istad.docuhub.feature.paper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PaperRequest(
        @NotNull
        @NotBlank
        String title,
        @NotNull
        @NotBlank
        String abstractText,
        @NotNull
        String fileUrl,
        @NotNull
        String thumbnailUrl,
        @NotNull
        @NotBlank
        List<String> categoryNames
) {
}
