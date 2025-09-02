package com.istad.docuhub.feature.studentDetail.dto;

public record UpdateStudentDetailRequest(
        String studentCardUrl,
        String university,
        String major,
        Integer yearsOfStudy,
        String userUuid
) {
}
