package com.istad.docuhub.feature.paper.service;

import com.istad.docuhub.feature.paper.dto.PaperRequest;

public interface PaperService {
    void createPaper(PaperRequest paperRequest);
}
