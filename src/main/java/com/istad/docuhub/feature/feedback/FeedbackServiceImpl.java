package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.domain.Feedback;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Random;


@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {
        return null;
    }

//    @Override
//    public FeedbackResponse createFeedback(FeedbackRequest feedbackRequest) {
//        if (feedbackRequest.feedbackText() == null || feedbackRequest.feedbackText().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid feedback text");
//        }
//        if (feedbackRequest.feedbackText().length() > 200) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback text is too long");
//        }
//
//        if (feedbackRequest.receiverUuid() == null || feedbackRequest.receiverUuid().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receiver UUID");
//        }
//
//        if (!userRepository.findByUuidAndIsDeletedFalse(feedbackRequest.receiverUuid())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver not found");
//        }
//        if(!userRepository.findByUuidAndIsDeletedFalse(feedbackRequest.advisorUuid())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor not found");
//        }
//
//        if (feedbackRequest.advisorUuid() == null || feedbackRequest.advisorUuid().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid advisor UUID");
//        }
//
//        if (feedbackRequest.paperUuid() == null || feedbackRequest.paperUuid().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid paper UUID");
//        }
//
//        if (paperRepository.findByUuid(feedbackRequest.paperUuid())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paper not found");
//        }
//        int id;
//        int retries = 0;
//        do {
//            if (retries++ > 50) {
//                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
//            }
//            id = new Random().nextInt(Integer.parseInt("1000000"));
//        } while (feedbackRepository.existsById(id));
//
//        Feedback feedback = new Feedback();
//        feedback.setId(id);
//        feedback.setFeedbackText(feedback.getFeedbackText());
//        feedback.setStatus(feedback.getStatus());
//        feedback
//
//        return null;
//    }
}
