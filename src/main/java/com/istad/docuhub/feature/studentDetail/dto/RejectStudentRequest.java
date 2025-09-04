package com.istad.docuhub.feature.studentDetail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectStudentRequest(
        @NotBlank(message = "User UUID is required")
        String userUuid,

        @NotBlank(message = "Reason is required")
        @Size(max = 500, message = "Reason must not exceed 500 characters")
        String reason
) {
}
