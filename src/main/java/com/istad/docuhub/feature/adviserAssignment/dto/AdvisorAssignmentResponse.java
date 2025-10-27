package com.istad.docuhub.feature.adviserAssignment.dto;


import lombok.Builder;

import java.time.LocalDate;
@Builder
public record AdvisorAssignmentResponse(
        String assignmentUuid,
        String status,
        LocalDate deadline,
        LocalDate assignedDate,
        PaperBriefResponse paper,
        StudentBriefResponse student
) {

}





