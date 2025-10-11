package com.istad.docuhub.feature.adviserAssignment.dto;

import java.time.LocalDate;

public record AdvisorAssignmentResponse(
        String assignmentUuid,
        String status,
        LocalDate deadline,
        LocalDate assignedDate,
        PaperBriefResponse paper,
        StudentBriefResponse student
) {


}





