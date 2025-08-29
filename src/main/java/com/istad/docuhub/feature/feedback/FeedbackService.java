package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;

public interface FeedbackService {
    FeedbackResponse createFeedback(FeedbackRequest feedbackRequest);
}
