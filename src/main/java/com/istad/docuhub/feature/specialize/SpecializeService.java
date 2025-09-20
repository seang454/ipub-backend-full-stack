package com.istad.docuhub.feature.specialize;

import com.istad.docuhub.feature.specialize.dto.SpecializeRequest;
import com.istad.docuhub.feature.specialize.dto.SpecializeResponse;

import java.util.List;

public interface SpecializeService {

    SpecializeResponse createSpecialize(SpecializeRequest request);

    List<SpecializeResponse> getAllSpecializes();

    SpecializeResponse getSpecializeByUuid(String uuid);

    SpecializeResponse updateSpecialize(String uuid, SpecializeRequest request);

    void deleteSpecialize(String uuid);
}
