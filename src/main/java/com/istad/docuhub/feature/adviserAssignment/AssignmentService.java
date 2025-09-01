package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;

import java.time.LocalDate;

public interface AssignmentService {
    AdviserAssignmentResponse assignAdviserToPaper(AdviserAssignmentRequest request);
    public AdviserAssignmentResponse reassignAdviser(String paperUuid, String newAdviserUuid, String adminUuid, LocalDate newDeadline);
}
