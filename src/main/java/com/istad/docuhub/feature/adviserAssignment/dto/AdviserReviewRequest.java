package com.istad.docuhub.feature.adviserAssignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record AdviserReviewRequest(

        @NotBlank(message = "Assignment UUID is required")
        String assignmentUuid,

        @NotBlank(message = "Status is required")
        @Pattern(
                regexp = "APPROVED|REJECTED",
                message = "Status must be either APPROVED or REJECTED"
        )
        String status,   // adviser decision

        String comment   // optional feedback
) {
}
