package com.istad.docuhub.feature.adviserAssignment.dto;

public record RejectPaperRequest(
        String paperUuid,
        String reason
) {
}
