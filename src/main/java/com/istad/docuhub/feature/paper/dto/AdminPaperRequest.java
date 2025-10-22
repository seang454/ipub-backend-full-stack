package com.istad.docuhub.feature.paper.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminPaperRequest(

        // Optional — only update if provided
        String title,

        // Optional — only update if provided
        String abstractText,

        // Optional — only update if provided
        String fileUrl,

        // Optional — only update if provided
        String thumbnailUrl,

        // Optional — can be empty or null
        List<String> category
) {
}

