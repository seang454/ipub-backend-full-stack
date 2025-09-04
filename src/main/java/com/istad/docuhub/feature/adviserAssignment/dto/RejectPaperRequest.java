package com.istad.docuhub.feature.adviserAssignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RejectPaperRequest(

        @NotBlank(message = "Paper UUID is required")
        String paperUuid,

        @NotBlank(message = "Reason is required")
        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason
) {
}
