package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailRequest;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import com.istad.docuhub.feature.adviserDetail.dto.UpdateAdviserDetailRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdviserService {

    AdviserDetailResponse createAdviserDetail(AdviserDetailRequest adviserDetailRequest);

    AdviserDetailResponse getAdviserDetailByUuid(String uuid);

    AdviserDetailResponse updateAdviserDetailByUuid(String uuid, UpdateAdviserDetailRequest updateRequest);

    void deleteAdviserDetail(String uuid);

    List<AdviserDetailResponse> getAllAdviserDetails();

    AdviserDetailResponse updateByToken( UpdateAdviserDetailRequest updateRequest);

    Page<AdviserAssignmentResponse> getAllAssignment();
}
