package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.feature.feedback.dto.FeedBackUpdate;
import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FeedbackService {
    void createFeedback(FeedbackRequest feedbackRequest);
    Page<FeedbackResponse> getAllFeedBack(Pageable pageable);
    void updateFeedbackStatus(String paperUuid,FeedBackUpdate feedBackUpdate);

    FeedbackResponse getFeedbackByPaperUuid(String paperUuid);
}
