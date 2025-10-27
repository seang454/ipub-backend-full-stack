package com.istad.docuhub.feature.studentDetail.dto;

import lombok.Builder;

@Builder
public record StudentResponse(
        String uuid,
        String studentCardUrl,
        String university,
        String major,
        Integer yearsOfStudy,
        Boolean isStudent,
        int status,
        String userUuid

) {
}