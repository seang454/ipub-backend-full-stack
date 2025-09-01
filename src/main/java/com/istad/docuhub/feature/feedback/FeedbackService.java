package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;

import java.util.List;

public interface FeedbackService {
    void createFeedback(FeedbackRequest feedbackRequest);
    List<FeedbackResponse> getAllFeedBack();
}
