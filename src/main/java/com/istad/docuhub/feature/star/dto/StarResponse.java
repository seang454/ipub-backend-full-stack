package com.istad.docuhub.feature.star.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StarResponse(
        Integer id,
        String uuid,
        LocalDate staredAt,
        String paperTitle,
        String userFullName
) {
}
