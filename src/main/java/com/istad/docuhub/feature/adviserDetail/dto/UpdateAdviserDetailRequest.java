package com.istad.docuhub.feature.adviserDetail.dto;

import com.istad.docuhub.domain.Specialize;
import com.istad.docuhub.enums.AdviserSpecialize;

import java.util.List;

public record UpdateAdviserDetailRequest(
        Integer experienceYears,
        String linkedinUrl,
        String office,
        String socialLinks,
        String status
) {}
