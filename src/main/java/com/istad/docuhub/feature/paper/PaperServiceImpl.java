package com.istad.docuhub.feature.paper;


import com.istad.docuhub.domain.Category;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.Star;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.category.CategoryRepository;
import com.istad.docuhub.feature.media.MediaService;
import com.istad.docuhub.feature.paper.dto.AdminPaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.star.StarRepository;
import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import com.istad.docuhub.utils.FeedBackStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final MediaService mediaService;
    private final StarRepository starRepository;

    @Override
    public void createPaper(PaperRequest paperRequest) {
        // Find author
        CurrentUser subId = userService.getCurrentUserSub();
        User author = userRepository.findByUuid(subId.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int id;
        int retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (paperRepository.existsById(id));

        // Find category by name and get its UUID
        String categoryName = paperRequest.categoryNames().getFirst();
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryName));

        // Create paper with auto-generated ID and category UUID
        Paper paper = Paper.builder().id(id).uuid(UUID.randomUUID().toString()).title(paperRequest.title()).abstractText(paperRequest.abstractText()).fileUrl(paperRequest.fileUrl()).thumbnailUrl(paperRequest.thumbnailUrl()).author(author).category(category).status("PENDING").submittedAt(LocalDate.now()).createdAt(LocalDate.now()).downloadCount(0).isApproved(false).isDeleted(false).isPublished(false).build();

        paperRepository.save(paper);
    }

    @Override
    public Page<PaperResponse> getAllPapersIsApprovedForAuthor(Pageable pageable) {
        CurrentUser subId = userService.getCurrentUserSub();
        Page<Paper> papers = paperRepository.findByAuthor_UuidAndIsDeletedFalseAndIsApprovedTrue(subId.id(), pageable);
        return papers.map(p -> new PaperResponse(p.getUuid(), p.getTitle(), p.getAbstractText(), p.getFileUrl(), p.getThumbnailUrl(), p.getAuthor().getUuid(), List.of(p.getCategory().getName()), p.getStatus(), p.getIsApproved(), p.getSubmittedAt(), p.getCreatedAt(), p.getIsPublished(), p.getPublishedAt(), p.getDownloadCount()));
    }

    @Override
    public void deletePaperByAuthor(String uuid) {
        CurrentUser subId = userService.getCurrentUserSub();
        Paper paper = paperRepository.findByUuid(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));
        if (!paper.getAuthor().getUuid().equals(subId.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to delete this paper");
        } else {
            paper.setIsDeleted(true);
            paperRepository.save(paper);
        }
    }

    @Override
    public PaperResponse updatePaperByAuthor(String uuid, PaperRequest paperRequest) {
        Paper paper = paperRepository.findByUuidAndIsDeletedFalseAndIsApprovedFalse(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found or already approved"));
        CurrentUser subId = userService.getCurrentUserSub();
        if (!paper.getAuthor().getUuid().equals(subId.id())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to update this paper");
        } else {
            // Find category by name and get its UUID
            String categoryName = paperRequest.categoryNames().getFirst();
            Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryName));
            paper.setTitle(paperRequest.title());
            paper.setAbstractText(paperRequest.abstractText());
            paper.setFileUrl(paperRequest.fileUrl());
            paper.setThumbnailUrl(paperRequest.thumbnailUrl());
            paper.setCategory(category);
            paperRepository.save(paper);

            return new PaperResponse(paper.getUuid(), paper.getTitle(), paper.getAbstractText(), paper.getFileUrl(), paper.getThumbnailUrl(), paper.getAuthor().getUuid(), List.of(paper.getCategory().getName()), paper.getStatus(), paper.getIsApproved(), paper.getSubmittedAt(), paper.getCreatedAt(), paper.getIsPublished(), paper.getPublishedAt(), paper.getDownloadCount());
        }
    }

    @Override
    public void publishPaperByUuid(String uuid) {
        CurrentUser subId = userService.getCurrentUserSub();
        String authorUuid = subId.id();
        Paper paper = paperRepository.findPaperByUuidAndAuthor_Uuid(uuid, authorUuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper is  not found or already published"));
        paper.setIsPublished(true);
        paper.setPublishedAt(LocalDate.now());
        paperRepository.save(paper);
    }

    @Override
    public List<StarResponse> getAllStarOfPapers() {
        CurrentUser subId = userService.getCurrentUserSub();
        List<Paper> papers = paperRepository.findPaperByAuthor_UuidAndIsDeletedFalseAndIsApprovedTrue(subId.id()).stream().toList();
        List<Star> stars = starRepository.findStarByPaper_UuidIn(papers.stream().map(Paper::getUuid).toList());
        return stars.stream().map(
                star -> new StarResponse(
                        star.getPaper().getUuid(),
                        star.getUser().getUuid()
                )
        ).toList(
        );
    }

    @Override
    public void publishPaperByPaperUuid(String paperUuid) {
        CurrentUser subId = userService.getCurrentUserSub();
        User author = userRepository.findByUuid(subId.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Paper paper = paperRepository.findPaperByUuidAndAuthor_UuidAndIsApprovedTrueAndIsDeletedFalseAndIsPublishedFalseAndStatus(paperUuid, author.getUuid(), FeedBackStatus.APPROVED.toString())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper is not found or already published"));
        paper.setIsPublished(true);
        paper.setPublishedAt(LocalDate.now());
        paperRepository.save(paper);
    }

    @Override
    public PaperResponse getPaperById(String Uuid) {
        Paper paper = paperRepository.findByUuid(Uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));
        return new PaperResponse(paper.getUuid(), paper.getTitle(), paper.getAbstractText(), paper.getFileUrl(), paper.getThumbnailUrl(), paper.getAuthor().getUuid(), List.of(paper.getCategory().getName()), paper.getStatus(), paper.getIsApproved(), paper.getSubmittedAt(), paper.getCreatedAt(), paper.getIsPublished(), paper.getPublishedAt(), paper.getDownloadCount());
    }

    @Override
    public Page<PaperResponse> getAllPapersIsPublished(Pageable pageable) {
        Page<Paper> paper = paperRepository.findByIsDeletedIsFalseAndIsApprovedTrueAndIsPublishedIsTrue(pageable);
        return paper.map(p -> new PaperResponse(p.getUuid(), p.getTitle(), p.getAbstractText(), p.getFileUrl(), p.getThumbnailUrl(), p.getAuthor().getUuid(), List.of(p.getCategory().getName()), p.getStatus(), p.getIsApproved(), p.getSubmittedAt(), p.getCreatedAt(), p.getIsPublished(), p.getPublishedAt(), p.getDownloadCount()));
    }

    @Override
    public Page<PaperResponse> getPapersByAuthor(Pageable pageable) {
        CurrentUser subId = userService.getCurrentUserSub();
        Page<Paper> papers = paperRepository.findByAuthor_UuidAndIsDeletedFalse(subId.id(), pageable);
        return papers.map(p -> new PaperResponse(p.getUuid(), p.getTitle(), p.getAbstractText(), p.getFileUrl(), p.getThumbnailUrl(), p.getAuthor().getUuid(), List.of(p.getCategory().getName()), p.getStatus(), p.getIsApproved(), p.getSubmittedAt(), p.getCreatedAt(), p.getIsPublished(), p.getPublishedAt(), p.getDownloadCount()));
    }

    @Override
    public Page<PaperResponse> getAllPapersIsApproved(Pageable pageable) {
        Page<Paper> papers = paperRepository.findByIsDeletedIsFalseAndIsApprovedTrue(pageable);
        return papers.map(p -> new PaperResponse(p.getUuid(), p.getTitle(), p.getAbstractText(), p.getFileUrl(), p.getThumbnailUrl(), p.getAuthor().getUuid(), List.of(p.getCategory().getName()), p.getStatus(), p.getIsApproved(), p.getSubmittedAt(), p.getCreatedAt(), p.getIsPublished(), p.getPublishedAt(), p.getDownloadCount()));
    }

    @Override
    public Page<PaperResponse> getAllPapersIsPending(Pageable pageable) {
        Page<Paper> papers = paperRepository.findByIsApprovedFalse(pageable);
        return papers.map(p -> new PaperResponse(p.getUuid(), p.getTitle(), p.getAbstractText(), p.getFileUrl(), p.getThumbnailUrl(), p.getAuthor().getUuid(), List.of(p.getCategory().getName()), p.getStatus(), p.getIsApproved(), p.getSubmittedAt(), p.getCreatedAt(), p.getIsPublished(), p.getPublishedAt(), p.getDownloadCount()));
    }

    @Override
    public Page<PaperResponse> getAllPaper(Pageable pageable) {
        Page<Paper> papers = paperRepository.findAll(pageable);
        return papers.map(p -> new PaperResponse(p.getUuid(), p.getTitle(), p.getAbstractText(), p.getFileUrl(), p.getThumbnailUrl(), p.getAuthor().getUuid(), List.of(p.getCategory().getName()), p.getStatus(), p.getIsApproved(), p.getSubmittedAt(), p.getCreatedAt(), p.getIsPublished(), p.getPublishedAt(), p.getDownloadCount()));
    }

    @Override
    public void deletePaperById(String uuid) {
        Paper paper = paperRepository.findByUuid(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));
        paper.setIsDeleted(true);
        paperRepository.save(paper);
    }

    // upate by thong
    @Override
    public void updatePaperPartiallyByAdmin(String uuid, AdminPaperRequest paperRequest) {
        Paper paper = paperRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));

        // Update title if provided
        if (paperRequest.title() != null && !paperRequest.title().isBlank()) {
            paper.setTitle(paperRequest.title());
        }

        // Update abstract if provided
        if (paperRequest.abstractText() != null && !paperRequest.abstractText().isBlank()) {
            paper.setAbstractText(paperRequest.abstractText());
        }

        // Update file URL if provided
        if (paperRequest.fileUrl() != null && !paperRequest.fileUrl().isBlank()) {
            String oldFileUrl = paper.getFileUrl();
            if (oldFileUrl != null) {
                mediaService.deleteMedia(oldFileUrl);
            }
            paper.setFileUrl(paperRequest.fileUrl());
        }

        // Update thumbnail URL if provided
        if (paperRequest.thumbnailUrl() != null && !paperRequest.thumbnailUrl().isBlank()) {
            String oldThumbnailUrl = paper.getThumbnailUrl();
            if (oldThumbnailUrl != null) {
                mediaService.deleteMedia(oldThumbnailUrl);
            }
            paper.setThumbnailUrl(paperRequest.thumbnailUrl());
        }

        // Update category if provided
        if (paperRequest.category() != null && !paperRequest.category().isEmpty()) {
            String categoryName = paperRequest.category().getFirst();
            Category category = categoryRepository.findByName(categoryName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found: " + categoryName));
            paper.setCategory(category);
        }

        paperRepository.save(paper);
    }

}

