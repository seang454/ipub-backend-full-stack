package com.istad.docuhub.feature.paper.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record PaperResponse(
        String uuid,
        String title,
        String abstractText,
        String fileUrl,
        String thumbnailUrl,
        String authorUuid,
        List<String> categoryNames,
        String status,
        Boolean isApproved,
        LocalDate submittedAt,
        LocalDate createdAt,
        Boolean isPublished,
        LocalDate publishedAt,
        Integer downloads
) {
}
