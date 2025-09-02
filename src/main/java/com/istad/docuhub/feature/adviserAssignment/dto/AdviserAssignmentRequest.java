package com.istad.docuhub.feature.adviserAssignment.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AdviserAssignmentRequest(
        String paperUuid,
        String adviserUuid,
        String adminUuid,
        LocalDate deadline
) {
}
