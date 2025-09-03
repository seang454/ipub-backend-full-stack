package com.istad.docuhub.feature.studentDetail.dto;

public record StudentDetailResponse(
        String uuid,
        String studentCardUrl,
        String university,
        String major,
        Integer yearsOfStudy,
        String userUuid
) {
}
