package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.domain.Feedback;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.feedback.dto.FeedBackUpdate;
import com.istad.docuhub.feature.feedback.dto.FeedbackRequest;
import com.istad.docuhub.feature.feedback.dto.FeedbackResponse;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import com.istad.docuhub.utils.FeedBackStatus;
import com.istad.docuhub.utils.PaperStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    @Transactional
    @Override
    public void createFeedback(FeedbackRequest feedbackRequest) {

        CurrentUser subId = userService.getCurrentUserSub();
        if (subId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User advisor = userRepository.findByUuidAndIsDeletedFalse(subId.id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor not found")
        );

        Paper paper = paperRepository.findByUuidAndIsDeletedFalseAndIsApprovedFalse(feedbackRequest.paperUuid()).orElseThrow(
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
        feedback.setCreatedAt(LocalDate.now());
        feedback.setUpdatedAt(null);
        feedback.setFileUrl(feedbackRequest.fileUrl());
        feedback.setPaper(paper);
        feedback.setAdvisor(advisor);
        feedback.setReceiver(receiver);

        if (feedbackRequest.status() == FeedBackStatus.APPROVED) {
            feedback.setStatus(FeedBackStatus.APPROVED);
            feedback.setDeadline(null);
            paper.setIsApproved(true);
            paper.setStatus(PaperStatus.APPROVED.toString());
        } else {
            feedback.setStatus(FeedBackStatus.REVISION);
            feedback.setDeadline(feedbackRequest.deadline());
            paper.setIsApproved(false);
            paper.setStatus(PaperStatus.REJECTED.toString());
        }
        paperRepository.save(paper);
        feedbackRepository.save(feedback);
    }

    @Override
    public Page<FeedbackResponse> getAllFeedBack(Pageable pageable) {
        return feedbackRepository.findAll(pageable).map(
                feedback -> new FeedbackResponse(
                        feedback.getFeedbackText(),
                        feedback.getStatus().toString(),
                        feedback.getPaper().getUuid(),
                        feedback.getFileUrl(),
                        feedback.getDeadline(),
                        feedback.getAdvisor().getFullName(),
                        feedback.getReceiver().getFullName(),
                        feedback.getCreatedAt(),
                        feedback.getUpdatedAt()
                )
        );
    }

    @Override
    public void updateFeedbackStatus(String paperUuid, FeedBackUpdate feedBackUpdate) {
        CurrentUser subId = userService.getCurrentUserSub();
        if (subId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        User advisor = userRepository.findByUuidAndIsDeletedFalse(subId.id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor not found")
        );
        Paper paper = paperRepository.findByUuidAndIsDeletedFalseAndIsApprovedFalse(paperUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paper not found")
        );
        Feedback feedback = feedbackRepository.findByPaper_Uuid(paperUuid);
        if (!Objects.equals(paper.getAssignedId().getAdvisor().getUuid(), advisor.getUuid())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Advisor is not assigned to the paper");
        }
        feedback.setFeedbackText(feedback.getFeedbackText());
        feedback.setStatus(feedback.getStatus());
        feedback.setUpdatedAt(LocalDate.now());
        feedbackRepository.save(feedback);
    }

    @Override
    public FeedbackResponse getFeedbackByPaperUuid(String paperUuid) {
        CurrentUser subId = userService.getCurrentUserSub();
        if (subId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        Feedback feedback = feedbackRepository.findByPaper_Uuid(paperUuid);
        return new FeedbackResponse(
                feedback.getFeedbackText(),
                feedback.getStatus().toString(),
                feedback.getPaper().getUuid(),
                feedback.getFileUrl(),
                feedback.getDeadline(),
                feedback.getAdvisor().getFullName(),
                feedback.getReceiver().getFullName(),
                feedback.getCreatedAt(),
                feedback.getUpdatedAt()
        );
    }
}
