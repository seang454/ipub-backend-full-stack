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
import java.util.Objects;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;

    @Override
    public void createFeedback(FeedbackRequest feedbackRequest) {


        User advisor = userRepository.findByUuidAndIsDeletedFalse(feedbackRequest.advisorUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor not found")
        );

        Paper paper = paperRepository.findByUuid(feedbackRequest.paperUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paper not found")
        );

        User receiver = userRepository.findByUuidAndIsDeletedFalse(paper.getAuthor().getUuid()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver not found")
        );

        if (!paper.getAuthor().getUuid().equals(receiver.getUuid())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Receiver is not the author of the paper");
        }

        if (!Objects.equals(paper.getAssignedId().getAdvisor().getUuid(), advisor.getUuid())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor is not assigned to the paper");
        }

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
        feedback.setFeedbackText(feedbackRequest.feedbackText());
        feedback.setStatus(feedbackRequest.status());
        feedback.setDeadline(feedbackRequest.deadline());
        feedback.setCreatedAt(LocalDate.now());
        feedback.setUpdatedAt(null);
        feedback.setFileUrl(feedbackRequest.fileUrl());
        feedback.setPaper(paper);
        feedback.setAdvisor(advisor);
        feedback.setReceiver(receiver);
        paper.setIsApproved(true);
        paper.setStatus(feedbackRequest.status());

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
