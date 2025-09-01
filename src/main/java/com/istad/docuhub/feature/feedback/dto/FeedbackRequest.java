package com.istad.docuhub.feature.feedback.dto;

import java.time.LocalDate;

public record FeedbackRequest(
        String feedbackText,
        String status,
        String paperUuid,
        String advisorUuid,
        String receiverUuid,
        String fileUrl,
        LocalDate deadline
) {
}
