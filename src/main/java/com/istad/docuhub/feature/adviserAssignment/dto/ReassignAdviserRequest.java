package com.istad.docuhub.feature.adviserAssignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ReassignAdviserRequest(

        @NotBlank(message = "Paper UUID is required")
        String paperUuid,

        @NotBlank(message = "New Adviser UUID is required")
        String newAdviserUuid,

        @NotBlank(message = "Admin UUID is required")
        String adminUuid,

        @NotNull(message = "Deadline is required")
        @FutureOrPresent(message = "Deadline must be today or in the future")
        LocalDate deadline,

        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason // optional: why reassigned
) {
}

