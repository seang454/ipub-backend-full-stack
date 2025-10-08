package com.istad.docuhub.feature.user.dto;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse(
     String slug,
     String uuid,
     String userName,
     String gender,
     String email,
     String fullName,
     String firstName,
     String lastName,
     String imageUrl,
     String status,
     LocalDate createDate,
     LocalDate updateDate,
     String bio,
     String address,
     String contactNumber,
     String telegramId,
     Boolean isUser,
     Boolean isAdmin,
     Boolean isStudent,
     Boolean isAdvisor,
     Boolean isActive
) {
}
