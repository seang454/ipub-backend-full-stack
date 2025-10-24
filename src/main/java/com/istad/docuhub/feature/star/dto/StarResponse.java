package com.istad.docuhub.feature.star.dto;

import lombok.Builder;

@Builder
public record StarResponse(
        String paperUuid,
        String userUuid,
        boolean starred,
        String message,
        long starCount
) {
    public static StarResponse starred(String paperUuid, String userUuid, long starCount) {
        return StarResponse.builder()
                .paperUuid(paperUuid)
                .userUuid(userUuid)
                .starred(true)
                .message("Paper starred successfully")
                .starCount(starCount)
                .build();
    }

    public static StarResponse unstarred(String paperUuid, String userUuid, long starCount) {
        return StarResponse.builder()
                .paperUuid(paperUuid)
                .userUuid(userUuid)
                .starred(false)
                .message("Paper unstarred successfully")
                .starCount(starCount)
                .build();
    }
}