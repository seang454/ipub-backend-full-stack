package com.istad.docuhub.feature.adviserAssignment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public record PaperBriefResponse(
        String uuid,
       String title,
       String fileUrl,
       String thumbnailUr)
    {}