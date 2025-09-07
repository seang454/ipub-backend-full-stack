package com.istad.docuhub.feature.comment.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CommentResponse(


        Integer id,
        String uuid,
        String content,
        LocalDate createdAt,
        Integer paperId,
        String paperTitle,
        Integer userId,
        String userFullName,
        String userImageUrl

) {
}
