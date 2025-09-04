package com.istad.docuhub.feature.paper.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

@Builder
public record AdminPaperRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Abstract is required")
        @Size(max = 2000, message = "Abstract must not exceed 2000 characters")
        String abstractText,

        @NotBlank(message = "File URL is required")
        String fileUrl,

        String thumbnailUrl, // optional

        @NotEmpty(message = "At least one category is required")
        List<@NotBlank(message = "Category name cannot be blank") String> category
) {
}

