package com.istad.docuhub.feature.comment.dto;

import java.time.LocalDate;

public record CommentResponse(
        String uuid,
        String content,
        LocalDate createdAt,
        String userUuid,
        String userName,
        String paperUuid,
        String paperTitle
) {
}
