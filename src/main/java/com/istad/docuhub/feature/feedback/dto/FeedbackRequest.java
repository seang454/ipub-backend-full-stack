package com.istad.docuhub.feature.feedback.dto;

public record FeedbackRequest(
        String feedbackText,
        String status,
        String paperUuid,
        String advisorUuid,
        String receiverUuid,
        String fileUrl,
        String deadline
) {
}
