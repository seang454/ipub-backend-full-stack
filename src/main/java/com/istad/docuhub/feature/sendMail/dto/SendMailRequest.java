package com.istad.docuhub.feature.sendMail.dto;

import jakarta.validation.constraints.NotNull;

public record SendMailRequest(
        @NotNull
        String paperUuid,
        @NotNull
        String rejectionReason
) {
}
