package com.istad.docuhub.feature.adviserAssignment.dto;

public record AdviserReviewRequest(
        String assignmentUuid,
        String  status,   // adviser decision
        String comment      // optional feedback
) {
}
