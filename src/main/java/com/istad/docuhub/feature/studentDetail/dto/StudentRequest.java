package com.istad.docuhub.feature.studentDetail.dto;

public record StudentRequest(
        String studentCardUrl,
        String university,
        String major,
        String yearsOfStudy,
        String userUuid
) {
}
