package com.istad.docuhub.feature.user.dto;

import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import com.istad.docuhub.feature.studentDetail.dto.StudentResponse;

public record UserProfileResponse(
        UserResponse user,
        StudentResponse student,
        AdviserDetailResponse adviser
) {
}
