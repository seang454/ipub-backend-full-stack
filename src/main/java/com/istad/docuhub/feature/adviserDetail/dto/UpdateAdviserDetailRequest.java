package com.istad.docuhub.feature.adviserDetail.dto;


public record UpdateAdviserDetailRequest(
        Integer experienceYears,
        String linkedinUrl,
        String office,
        String socialLinks,
        String status
) {}
