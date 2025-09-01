package com.istad.docuhub.feature.adviserAssignment;

import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentRequest;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;

public interface AssignmentService {
    AdviserAssignmentResponse assignAdviserToPaper(AdviserAssignmentRequest request);
}
