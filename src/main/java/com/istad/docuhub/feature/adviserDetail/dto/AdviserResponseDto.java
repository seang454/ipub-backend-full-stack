package com.istad.docuhub.feature.adviserDetail.dto;

public record AdviserResponseDto(
         Long id,
         Integer yearsExperience,
         String idCard,
         String linkedinUrl,
         String publication,
         String address,
         String certifications,
         String availability,
         String socialLinks,
         String userUuid

){
}
