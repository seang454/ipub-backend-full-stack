package com.istad.docuhub.feature.star.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StarResponse(
        Integer userId,
        String userUuid,
        String fullName,
        String imageUrl,
        LocalDate starredAt
) {
}
