package com.istad.docuhub.feature.feedback.dto;

public record FeedbackResponse(
        String feedbackText,
        String status,
        String paperUuid,
        String fileUrl,
        String deadline,
        String advisorName,
        String receiverName,
        String createdAt,
        String updatedAt
) {
}
