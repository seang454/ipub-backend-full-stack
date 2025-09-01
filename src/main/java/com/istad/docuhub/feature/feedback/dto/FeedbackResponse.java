package com.istad.docuhub.feature.feedback.dto;

import java.time.LocalDate;

public record FeedbackResponse(
        String feedbackText,
        String status,
        String paperUuid,
        String fileUrl,
        LocalDate deadline,
        String advisorName,
        String receiverName,
        LocalDate createdAt,
        LocalDate updatedAt
) {
}
