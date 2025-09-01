package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailRequest;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;

import java.util.List;

public interface AdviserService {

    AdviserDetailResponse createAdviserDetail(AdviserDetailRequest adviserDetailRequest);

    AdviserDetailResponse getAdviserDetailByUuid(String uuid);

    AdviserDetailResponse updateAdviserDetail(String uuid, AdviserDetailRequest adviserDetailRequest);

    void deleteAdviserDetail(String uuid);

    List<AdviserDetailResponse> getAllAdviserDetails();
}
