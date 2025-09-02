package com.istad.docuhub.feature.studentDetail.dto;

public record StudentResponse(
        String uuid,
        String studentCardUrl,
        String university,
        String major,
        Integer yearsOfStudy,
        Boolean isStudent,
        String userUuid
) {
}