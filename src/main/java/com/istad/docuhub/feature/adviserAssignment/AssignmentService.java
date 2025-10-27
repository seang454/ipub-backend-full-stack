package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.feature.adviserAssignment.dto.*;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentService {
    AdviserAssignmentResponse assignAdviserToPaper(AdviserAssignmentRequest request);
    AdviserAssignmentResponse reassignAdviser(String paperUuid, String newAdviserUuid, String adminUuid, LocalDate newDeadline);
    AdviserAssignmentResponse reviewPaperByAdviser(AdviserReviewRequest reviewRequest);
    PaperResponse rejectPaperByAdmin(RejectPaperRequest rejectRequest);
    List<AdviserAssignmentResponse> getAssignmentsByAdviserUuid(String adviserUuid);

    List<AdviserAssignmentResponse> getAllAssignmentsByAuthorUuid();

    List<AdviserAssignmentResponse> getAllAssignment();

    Page<AdvisorAssignmentResponse> getAssignmentsForCurrentAdviser(Pageable pageable);

    List<AdviserAssignmentResponse> getAssignmentsByAuthorUuid(String authorUuid);
}
