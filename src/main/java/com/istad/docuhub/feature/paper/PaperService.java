package com.istad.docuhub.feature.paper;

import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;

import java.util.List;

public interface PaperService {
    //Admin
    List<PaperResponse> getAllPapersIsApproved();
    List<PaperResponse> getAllPapersIsPending();
    List<PaperResponse> getAllPaper();
    void deletePaperById(String uuid);

    //Public
    PaperResponse getPaperById(String Uuid);
    List<PaperResponse> getAllPapersIsPublished();

    //Author
    List<PaperResponse> getPapersByAuthor();
    void createPaper(PaperRequest paperRequest);
    List<PaperResponse> getAllPapersIsApprovedForAuthor();
    void deletePaperByAuthor(String uuid);
}
