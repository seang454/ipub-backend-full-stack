package com.istad.docuhub.feature.paper.dto;

import java.time.LocalDate;
import java.util.List;

public record PaperResponse(
        String uuid,
        String title,
        String abstractText,
        String fileUrl,
        String authorUuid,
        List<String> categoryNames,
        String status,
        Boolean isApproved,
        LocalDate submittedAt,
        LocalDate createdAt,
        Boolean isPublished,
        LocalDate publishedAt
) {
}
