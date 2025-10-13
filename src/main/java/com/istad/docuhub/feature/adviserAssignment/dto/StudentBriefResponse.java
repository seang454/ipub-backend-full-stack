package com.istad.docuhub.feature.adviserAssignment.dto;

import lombok.Builder;
import lombok.Data;


@Builder
public record StudentBriefResponse(
        String uuid,
        String fullName,
        String imageUrl
) {}
