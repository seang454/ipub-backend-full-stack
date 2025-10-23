package com.istad.docuhub.feature.paper;

import com.istad.docuhub.feature.paper.dto.AdminPaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.star.dto.StarResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaperService {
    //Admin
    Page<PaperResponse> getAllPapersIsApproved(Pageable pageable);
    Page<PaperResponse> getAllPapersIsPending(Pageable pageable);
    Page<PaperResponse> getAllPaper(Pageable pageable);
    void deletePaperById(String uuid);
    void updatePaperPartiallyByAdmin(String uuid, AdminPaperRequest paperRequest);

    //Public
    PaperResponse getPaperById(String Uuid);
    Page<PaperResponse> getAllPapersIsPublished(Pageable pageable);

    //Author
    Page<PaperResponse> getPapersByAuthor(Pageable pageable);
    void createPaper(PaperRequest paperRequest);
    Page<PaperResponse> getAllPapersIsApprovedForAuthor(Pageable pageable);
    void deletePaperByAuthor(String uuid);
    PaperResponse updatePaperByAuthor(String uuid, PaperRequest paperRequest);

    void publishPaperByUuid(String uuid);

    List<StarResponse> getAllStarOfPapers();

    void publishPaperByPaperUuid(String paperUuid);
}
