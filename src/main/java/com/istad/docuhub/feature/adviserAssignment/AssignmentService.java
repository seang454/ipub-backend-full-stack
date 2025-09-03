package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserReviewRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.RejectPaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;

import java.time.LocalDate;
import java.util.List;

public interface AssignmentService {
    AdviserAssignmentResponse assignAdviserToPaper(AdviserAssignmentRequest request);
    AdviserAssignmentResponse reassignAdviser(String paperUuid, String newAdviserUuid, String adminUuid, LocalDate newDeadline);
    AdviserAssignmentResponse reviewPaperByAdviser(AdviserReviewRequest reviewRequest);
    PaperResponse rejectPaperByAdmin(RejectPaperRequest rejectRequest);
    List<AdviserAssignmentResponse> getAssignmentsByAdviserUuid(String adviserUuid);
}
