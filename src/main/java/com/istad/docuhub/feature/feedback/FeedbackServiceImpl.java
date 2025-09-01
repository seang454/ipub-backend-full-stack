package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.domain.Feedback;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public void createFeedback(FeedbackRequest feedbackRequest) {
        if (feedbackRequest.feedbackText() == null || feedbackRequest.feedbackText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid feedback text");
        }
        if (feedbackRequest.feedbackText().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feedback text is too long");
        }

        if (feedbackRequest.deadline().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid deadline");
        }

        // Validate receiver UUID
        if (feedbackRequest.receiverUuid() == null || feedbackRequest.receiverUuid().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid receiver UUID");
        }
        User receiver = userRepository.findByUuidAndIsDeletedFalse(feedbackRequest.receiverUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver not found")
        );

        // Validate advisor UUID
        if (feedbackRequest.advisorUuid() == null || feedbackRequest.advisorUuid().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid advisor UUID");
        }
        User advisor = userRepository.findByUuidAndIsDeletedFalse(feedbackRequest.advisorUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor not found")
        );


        if (feedbackRequest.paperUuid() == null || feedbackRequest.paperUuid().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid paper UUID");
        }

        Paper paper = paperRepository.findByUuid(feedbackRequest.paperUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paper not found")
        );

        int id;
        int retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (feedbackRepository.existsById(id));

        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setFeedbackText(feedback.getFeedbackText());
        feedback.setStatus(feedback.getStatus());
        feedback.setDeadline(feedback.getDeadline());
        feedback.setCreatedAt(LocalDate.now());
        feedback.setUpdatedAt(null);
        feedback.setFileUrl(feedback.getFileUrl());
        feedback.setPaper(paper);
        feedback.setAdvisor(advisor);
        feedback.setReceiver(receiver);

        feedbackRepository.save(feedback);
    }

    @Override
    public List<FeedbackResponse> getAllFeedBack() {

        List<Feedback> feedback = feedbackRepository.findAll();

        return feedback.stream()
                .map(
                        feedback1 -> new FeedbackResponse(
                                feedback1.getFeedbackText(),
                                feedback1.getStatus(),
                                feedback1.getPaper().getUuid(),
                                feedback1.getFileUrl(),
                                feedback1.getDeadline(),
                                feedback1.getAdvisor().getFullName(),
                                feedback1.getReceiver().getFullName(),
                                feedback1.getCreatedAt(),
                                feedback1.getUpdatedAt()
                        )
                ).toList();
    }
}
