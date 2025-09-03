package com.istad.docuhub.feature.paper.dto;

import java.util.List;

public record PaperRequest(
        String title,
        String abstractText,
        String fileUrl,
        List<String> categoryNames
) {
}
