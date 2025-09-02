package com.istad.docuhub.feature.star.dto;

import lombok.Builder;

@Builder
public record StarCountResponse(
        Integer paperId,
        Long starCount,
        boolean userHasStarred
) {
}
