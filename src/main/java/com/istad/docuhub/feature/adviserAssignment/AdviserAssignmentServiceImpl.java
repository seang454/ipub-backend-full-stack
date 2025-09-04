package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.domain.AdviserAssignment;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserReviewRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.RejectPaperRequest;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.sendMail.SendMailService;
import com.istad.docuhub.feature.sendMail.dto.SendMailRequest;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import com.istad.docuhub.utils.KeycloakUserDto;
import com.istad.docuhub.utils.KeycloakUserService;
import com.istad.docuhub.utils.PaperStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdviserAssignmentServiceImpl implements AssignmentService {

    private final AdviserAssignmentRepository adviserAssignmentRepository;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final SendMailService sendMailService;
    private final KeycloakUserService keycloakUserService;
    private final UserService userService;

    // assign adviesr
    @Override
    public AdviserAssignmentResponse assignAdviserToPaper(AdviserAssignmentRequest request) {

        // fetch paper
        Paper paper = paperRepository.findByUuid(request.paperUuid()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));

        // fetch adviser
        User adviser = userRepository.findByUuid(request.adviserUuid())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adviser Not Found"));
        // fetch admin
        CurrentUser currentUser = userService.getCurrentUserSub();
        User userAdmin = userRepository.findByUuid(currentUser.id())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin Not Found"));

        // ✅ Update paper status
        paper.setStatus("UNDER_REVIEW"); // pending adviser
        paper.setIsApproved(true); // by admin


        int id;
        int retire = 0;
        do {
            if (retire++ > 10) {
                throw new RuntimeException("Failed to generate unique ID after 10 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        }while (adviserAssignmentRepository.existsById(id));


        // create assignment
        AdviserAssignment assignment = new AdviserAssignment();
        assignment.setId(id);
        assignment.setUuid(UUID.randomUUID().toString());
        assignment.setPaper(paper);
        assignment.setAdvisor(adviser);
        assignment.setAdmin(userAdmin);
        assignment.setDeadline(request.deadline());
        assignment.setStatus("ASSIGNED");
        assignment.setAssignedDate(LocalDate.now());
        assignment.setUpdateDate(null);

        AdviserAssignment saved = adviserAssignmentRepository.save(assignment);
        AdviserAssignment assignUuid = adviserAssignmentRepository.findByUuid(saved.getUuid()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment Not Found"));
        paper.setAssignedId(assignUuid);
        paperRepository.save(paper);

        return AdviserAssignmentResponse.builder()
                .uuid(saved.getUuid())
                .paperUuid(saved.getPaper().getUuid())
                .adviserUuid(saved.getAdvisor().getUuid())
                .adminUuid(saved.getAdmin().getUuid())
                .deadline(saved.getDeadline())
                .status(saved.getStatus())
                .assignedDate(saved.getAssignedDate())
                .updateDate(saved.getUpdateDate())
                .build();
    }

    @Override
    public AdviserAssignmentResponse reassignAdviser(String paperUuid, String newAdviserUuid, String adminUuid, LocalDate newDeadline) {
        // fetch paper
        Paper paper = paperRepository.findByUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));

        // fetch new adviser
        User newAdviser = userRepository.findByUuid(newAdviserUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adviser Not Found"));

        // fetch admin
        User admin = userRepository.findByUuid(adminUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin Not Found"));

        // find current assignment (if any)
        AdviserAssignment currentAssignment = adviserAssignmentRepository.findByPaperUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No Current Adviser Assigned"));

        // mark old one as REASSIGNED
        currentAssignment.setStatus("REASSIGNED");
        currentAssignment.setUpdateDate(LocalDate.now());
        adviserAssignmentRepository.save(currentAssignment);

        // create new assignment
        AdviserAssignment newAssignment = new AdviserAssignment();
        newAssignment.setUuid(UUID.randomUUID().toString());
        newAssignment.setPaper(paper);
        newAssignment.setAdvisor(newAdviser);
        newAssignment.setAdmin(admin);
        newAssignment.setDeadline(newDeadline);
        newAssignment.setStatus("ASSIGNED");
        newAssignment.setAssignedDate(LocalDate.now());
        newAssignment.setUpdateDate(null);

        AdviserAssignment saved = adviserAssignmentRepository.save(newAssignment);

        return AdviserAssignmentResponse.builder()
                .uuid(saved.getUuid())
                .paperUuid(saved.getPaper().getUuid())
                .adviserUuid(saved.getAdvisor().getUuid())
                .adminUuid(saved.getAdmin().getUuid())
                .deadline(saved.getDeadline())
                .status(saved.getStatus())
                .assignedDate(saved.getAssignedDate())
                .updateDate(saved.getUpdateDate())
                .build();
    }

    // adviser review paper
    @Transactional
    @Override
    public AdviserAssignmentResponse reviewPaperByAdviser(AdviserReviewRequest reviewRequest) {
        // fetch assignment
        AdviserAssignment assignment = adviserAssignmentRepository.findByUuid(reviewRequest.assignmentUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment Not Found"));

        Paper paper = assignment.getPaper();

        if (reviewRequest.status().equals("APPROVED")) {
            paper.setStatus("APPROVED");
        } else {
            paper.setStatus("REJECTED");
        }

        paperRepository.save(paper);

        // update assignment
//        assignment.setStatus(reviewRequest.status() ? "APPROVED" : "REJECTED");
        assignment.setStatus(reviewRequest.status());
        assignment.setUpdateDate(LocalDate.now());
        // you can also store adviser comment if needed
        AdviserAssignment saved = adviserAssignmentRepository.save(assignment);

        return AdviserAssignmentResponse.builder()
                .uuid(saved.getUuid())
                .paperUuid(saved.getPaper().getUuid())
                .adviserUuid(saved.getAdvisor().getUuid())
                .adminUuid(saved.getAdmin().getUuid())
                .deadline(saved.getDeadline())
                .status(saved.getStatus())
                .assignedDate(saved.getAssignedDate())
                .updateDate(saved.getUpdateDate())
                .build();
    }


    // reject paper by admin ( take infor from keycloak )
    @Override
    @Transactional
    public PaperResponse rejectPaperByAdmin(RejectPaperRequest rejectRequest) {
        Paper paper = paperRepository.findByUuid(rejectRequest.paperUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));

        // Update paper status
        paper.setStatus(PaperStatus.ADMIN_REJECTED.name());
        paper.setIsApproved(false);
        paperRepository.save(paper);

        // Fetch author from Keycloak
//        String authorUuid = paper.getAuthor().getUuid();
//        KeycloakUserDto author = keycloakUserService.getUserById(authorUuid);

        // Prepare mail request
        SendMailRequest mailRequest = new SendMailRequest(rejectRequest.paperUuid(), rejectRequest.reason());

        // Send rejection email
        sendMailService.sendMailReject(mailRequest);

        return PaperResponse.builder()
                .uuid(paper.getUuid())
                .status(paper.getStatus())
                .isApproved(paper.getIsApproved())
                .build();
    }

    @Override
    public List<AdviserAssignmentResponse> getAssignmentsByAdviserUuid(String adviserUuid) {
        List<AdviserAssignment> assignments = adviserAssignmentRepository.findByAdvisorUuid(adviserUuid);

        if (assignments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No assignments found for this adviser");
        }

        return assignments.stream()
                .map(assignment -> AdviserAssignmentResponse.builder()
                        .uuid(assignment.getUuid())
                        .paperUuid(assignment.getPaper().getUuid())
                        .adviserUuid(assignment.getAdvisor().getUuid())
                        .adminUuid(assignment.getAdmin().getUuid())
                        .deadline(assignment.getDeadline())
                        .status(assignment.getStatus())
                        .assignedDate(assignment.getAssignedDate())
                        .updateDate(assignment.getUpdateDate())
                        .build()
                ).toList();
    }

}
