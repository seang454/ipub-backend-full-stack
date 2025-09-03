package com.istad.docuhub.feature.paper;

import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;

import java.util.List;

public interface PaperService {
    void createPaper(PaperRequest paperRequest);
    List<PaperResponse> getAllPapers();
    List<PaperResponse> getAllPapersIsPending();
    PaperResponse getPaperById(String Uuid);
    List<PaperResponse> getPapersByAuthor();
}
