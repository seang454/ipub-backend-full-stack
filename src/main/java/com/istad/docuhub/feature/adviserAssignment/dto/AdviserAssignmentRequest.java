package com.istad.docuhub.feature.adviserAssignment.dto;

import lombok.Builder;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;

@Builder
public record AdviserAssignmentRequest(

        @NotBlank(message = "Paper UUID is required")
        String paperUuid,

        @NotBlank(message = "Adviser UUID is required")
        String adviserUuid,

        @NotNull(message = "Deadline is required")
        @FutureOrPresent(message = "Deadline must be today or in the future")
        LocalDate deadline
) {
}

