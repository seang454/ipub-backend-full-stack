package com.istad.docuhub.feature.star.service.serviceImpl;

import com.istad.docuhub.Repository.PaperRepository;
import com.istad.docuhub.Repository.StarRepository;
import com.istad.docuhub.Repository.UserRepository;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.Star;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.star.dto.CreateStarRequest;
import com.istad.docuhub.feature.star.dto.DeleteStarRequest;
import com.istad.docuhub.feature.star.dto.StarCountResponse;
import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.star.mapper.StarMapper;
import com.istad.docuhub.feature.star.service.StarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class StarServiceImpl implements StarService {


    private final UserRepository userRepository;
    private final PaperRepository paperRepository;
    private final StarRepository starRepository;
    private final StarMapper starMapper;


    @Override
    public StarResponse starPaper(CreateStarRequest createStarRequest) {

        // Check if star already exists
        if (starRepository.existsByUserIdAndPaperId(createStarRequest.userId(), createStarRequest.paperId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already starred this paper");
        }

        Star star = new Star();

        star.setUuid(UUID.randomUUID().toString());
        star.setStaredAt(LocalDate.now());

        // Validation User
        User user = userRepository.findUserById(createStarRequest.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validation Paper
        Paper paper = paperRepository.findPaperById(createStarRequest.paperId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        // Additional validation - check if paper can be starred
        if (!paper.getIsPublished() || !paper.getIsApproved()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot star unpublished or unapproved paper");
        }

        star.setPaper(paper);
        star.setUser(user);

        Star saved = starRepository.save(star);

        return starMapper.toStarResponse(saved);
    }




    @Override
    public void unstarPaper(DeleteStarRequest deleteStarRequest) {

        if (!starRepository.existsByUserIdAndPaperId(deleteStarRequest.userId(), deleteStarRequest.paperId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Star not found or you don't have permission to remove it");
        }

        starRepository.deleteByUserIdAndPaperId(deleteStarRequest.userId(), deleteStarRequest.paperId());

    }





    @Override
    public StarResponse toggleStar(CreateStarRequest createStarRequest) {

        // Validation User
        User user = userRepository.findUserById(createStarRequest.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Validation Paper
        Paper paper = paperRepository.findPaperById(createStarRequest.paperId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        // Additional validation
        if (!paper.getIsPublished() || !paper.getIsApproved()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot star unpublished or unapproved paper");
        }

        // Check if star exists
        if (starRepository.existsByUserIdAndPaperId(createStarRequest.userId(), createStarRequest.paperId())) {
            // Remove star
            starRepository.deleteByUserIdAndPaperId(createStarRequest.userId(), createStarRequest.paperId());

            // Return response indicating unstarred
            return StarResponse.builder()
                    .paperTitle(paper.getTitle())
                    .userFullName(user.getFullName())
                    .build();
        } else {
            // Add star
            Star star = new Star();
            star.setUuid(UUID.randomUUID().toString());
            star.setStaredAt(LocalDate.now());
            star.setPaper(paper);
            star.setUser(user);

            Star saved = starRepository.save(star);
            return starMapper.toStarResponse(saved);
        }
    }





    @Override
    public StarCountResponse getStarInfo(Integer paperId, Integer userId) {
        // Validation Paper exists
        paperRepository.findPaperById(paperId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        Long starCount = starRepository.countByPaperId(paperId);
        boolean userHasStarred = starRepository.existsByUserIdAndPaperId(userId, paperId);

        return StarCountResponse.builder()
                .paperId(paperId)
                .starCount(starCount)
                .userHasStarred(userHasStarred)
                .build();
    }





    @Override
    public Page<StarResponse> getUserStarredPapers(Integer userId, int page, int size) {
        // Validation User
        userRepository.findUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Star> stars = starRepository.findByUserIdOrderByStaredAtDesc(userId, pageable);

        return stars.map(starMapper::toStarResponse);
    }



}
