package com.istad.docuhub.feature.adviserDetail.dto;

import lombok.Builder;

@Builder
public record AdviserDetailResponse(
         Integer yearsExperience,
         String linkedinUrl,
         String publication,
         String availability,
         String socialLinks,
         String userUuid
){
}
