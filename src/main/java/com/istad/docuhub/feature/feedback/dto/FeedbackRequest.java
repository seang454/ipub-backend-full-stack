package com.istad.docuhub.feature.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FeedbackRequest(
        @NotBlank @NotNull String feedbackText,
        @NotBlank @NotNull String status,
        @NotBlank @NotNull String paperUuid,
        @NotBlank @NotNull String advisorUuid,
        @NotBlank @NotNull String fileUrl,
        LocalDate deadline
) {
}
