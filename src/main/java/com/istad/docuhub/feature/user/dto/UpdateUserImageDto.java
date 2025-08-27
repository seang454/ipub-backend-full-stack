package com.istad.docuhub.feature.user.dto;

import lombok.Builder;

@Builder
public record UpdateUserImageDto(
        String imageUrl
) {
}
