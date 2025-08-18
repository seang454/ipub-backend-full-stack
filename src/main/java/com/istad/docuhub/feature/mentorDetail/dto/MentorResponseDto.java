package com.istad.docuhub.feature.mentorDetail.dto;

public record MentorResponseDto (
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
