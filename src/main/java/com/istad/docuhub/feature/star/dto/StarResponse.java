package com.istad.docuhub.feature.star.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StarResponse(
        String paperUuid,
        String userUuid
) {
}
