package com.istad.docuhub.feature.adviserDetail.dto;

import com.istad.docuhub.domain.Specialize;
import lombok.Builder;

import java.util.List;

@Builder
public record AdviserDetailResponse(
        String uuid,
        Integer experienceYears,
        String linkedinUrl,
        String office,
        String socialLinks,
        String status,
        String userUuid,
        List<Specialize> specialize
) {}

