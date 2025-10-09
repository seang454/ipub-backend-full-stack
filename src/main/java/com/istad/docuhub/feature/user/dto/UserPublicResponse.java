package com.istad.docuhub.feature.user.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record UserPublicResponse(
        String slug,
        String uuid,
        String gender,
        String fullName,
        String imageUrl,
        String status,
        LocalDate createDate,
        LocalDate updateDate,
        String bio,
        Boolean isUser,
        Boolean isAdmin,
        Boolean isStudent,
        Boolean isAdvisor
) {
}

