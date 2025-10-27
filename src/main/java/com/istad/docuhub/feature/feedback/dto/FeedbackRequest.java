package com.istad.docuhub.feature.feedback.dto;

import com.istad.docuhub.utils.FeedBackStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FeedbackRequest(
        String feedbackText,
        FeedBackStatus status,
        String paperUuid,
        String fileUrl,
        LocalDate deadline
) {
}
