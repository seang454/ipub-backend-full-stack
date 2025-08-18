package com.istad.docuhub.feature.media.dto;

import java.time.LocalDate;

public record MediaResponse(
        String name,
        String uri,
        Long size,
        LocalDate created_date
) {
}
