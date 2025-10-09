package com.istad.docuhub.feature.adviserAssignment.dto;

import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.user.dto.UserResponse;

public record AssignmentStudentPaperResponse(
        PaperResponse paperResponse,
        UserResponse userResponse
) {
}
