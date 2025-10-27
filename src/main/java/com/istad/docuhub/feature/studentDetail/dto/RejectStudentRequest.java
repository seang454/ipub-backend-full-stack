package com.istad.docuhub.feature.studentDetail.dto;

import com.istad.docuhub.enums.STATUS;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectStudentRequest(
        @NotBlank(message = "User UUID is required")
        String userUuid,

        @NotBlank(message = "Reason is required")
        @Size(max = 3000, message = "Reason must not exceed 3000 characters")
        String reason,

        String status


) {
}
