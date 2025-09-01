package com.istad.docuhub.feature.adviserDetail.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record AdviserDetailRequest(
        Integer experienceYears,
        String linkedinUrl,
        String publication,
        String socialLinks,
        String status,
        String userUuid,              // Link to User via UUID
        List<String> specializeUuids  // Link to Specialize entities via UUIDs

) {
}
