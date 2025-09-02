package com.istad.docuhub.feature.adviserAssignment.dto;

import java.time.LocalDate;

public record ReassignAdviserRequest(
        String paperUuid,
        String newAdviserUuid,
        String adminUuid,
        LocalDate deadline,
        String reason // optional: why reassigned
) {
}
