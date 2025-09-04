package com.istad.docuhub.feature.paper.dto;

import java.util.List;

public record AdminPaperRequest(
        String title,
        String abstractText,
        String fileUrl,
        String thumbnailUrl,
        List<String> category
) {
}
