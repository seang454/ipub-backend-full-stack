package com.istad.docuhub.feature.paper.dto;

import java.time.LocalDate;
import java.util.List;

public record PaperRequest(
        String title,
        String abstractText,
        String fileUrl,
        String authorUuid,
        List<String> categoryUuid
) {
}
