package com.istad.docuhub.feature.studentDetail.dto;

public record StudentDetailRequest(
        String studentCardUrl,
        String university,
        String major,
        Integer yearsOfStudy,
        String userUuid // link to User by uuid
) {
}
