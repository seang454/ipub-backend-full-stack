package com.istad.docuhub.feature.star.service;

import com.istad.docuhub.feature.star.dto.CreateStarRequest;
import com.istad.docuhub.feature.star.dto.DeleteStarRequest;
import com.istad.docuhub.feature.star.dto.StarCountResponse;
import com.istad.docuhub.feature.star.dto.StarResponse;
import org.springframework.data.domain.Page;

public interface StarService {


    //Add star to paper
    StarResponse starPaper(CreateStarRequest createStarRequest);


    //Remove star from paper
    void unstarPaper(DeleteStarRequest deleteStarRequest);


    //Toggle star (add if not exists, remove if exists)
    StarResponse toggleStar(CreateStarRequest createStarRequest);


    //Get star count and user's star status for a paper
    StarCountResponse getStarInfo(Integer paperId, Integer userId);


    //Get user's starred papers with pagination
    Page<StarResponse> getUserStarredPapers(Integer userId, int page, int size);

}
