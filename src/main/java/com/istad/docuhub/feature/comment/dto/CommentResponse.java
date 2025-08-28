package com.istad.docuhub.feature.comment.dto;

import java.time.LocalDate;

public record CommentResponse(

        Integer id,
        Integer userId,
        Integer paperId,
        String content,
        LocalDate createdDate

) {
}
