package com.istad.docuhub.feature.studentDetail.dto;

public record RejectStudentRequest(
        String userUuid,
        String reason
) {
}
