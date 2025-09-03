package com.istad.docuhub.feature.feedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FeedbackRequest(
        String feedbackText,
        String status,
        String paperUuid,
        String advisorUuid,
        String fileUrl,
        LocalDate deadline
) {
}
