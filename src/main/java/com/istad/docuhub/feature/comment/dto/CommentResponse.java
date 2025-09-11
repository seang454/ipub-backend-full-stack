package com.istad.docuhub.feature.comment.dto;

import java.time.LocalDate;
import java.util.List;

public record CommentResponse(
        String uuid,
        String content,
        LocalDate createdAt,
        String userUuid,      // This exists
        String paperUuid,
        String parentUuid,
        List<CommentResponse> replies,
        boolean isDeleted
        // userName field is missing
) {}