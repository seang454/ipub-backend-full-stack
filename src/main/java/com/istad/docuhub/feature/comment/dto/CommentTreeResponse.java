package com.istad.docuhub.feature.comment.dto;

import java.util.List;

public record CommentTreeResponse(
        String paperUuid,
        List<CommentResponse> comments
) {
}
