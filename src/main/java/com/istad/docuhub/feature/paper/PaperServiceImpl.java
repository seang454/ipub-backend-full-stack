package com.istad.docuhub.feature.paper;


import com.istad.docuhub.domain.Category;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.category.CategoryRepository;
import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void createPaper(PaperRequest paperRequest) {
        // Find author
        CurrentUser subId = userService.getCurrentUserSub();
        User author = userRepository.findByUuid(subId.id()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!author.getIsStudent() || author.getIsAdvisor()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Author must be a student");
        }

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
        Paper paper = Paper.builder()
                .id(id)
                .uuid(UUID.randomUUID().toString())
                .title(paperRequest.title())
                .abstractText(paperRequest.abstractText())
                .fileUrl(paperRequest.fileUrl())
                .thumbnailUrl(paperRequest.thumbnailUrl())
                .author(author)
                .category(category)
                .status("PENDING")
                .submittedAt(LocalDate.now())
                .createdAt(LocalDate.now())
                .downloadCount(0)
                .isApproved(false)
                .isDeleted(false)
                .isPublished(false)
                .build();

        paperRepository.save(paper);
    }

    @Override
    public List<PaperResponse> getAllPapersIsApprovedForAuthor() {
        CurrentUser subId = userService.getCurrentUserSub();
        List<Paper> papers = paperRepository.findByAuthor_UuidAndIsDeletedFalseAndIsApprovedTrue((subId.id()));
        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getThumbnailUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()
                )).toList();
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
    public List<PaperResponse> getAllPapersIsApproved() {
        List<Paper> papers = paperRepository.findByIsDeletedIsFalseAndIsApprovedTrue();
        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getThumbnailUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()
                )).toList();
    }

    @Override
    public List<PaperResponse> getAllPapersIsPublished() {
        List<Paper> papers = paperRepository.findByIsDeletedIsFalseAndIsApprovedTrueAndIsPublishedIsTrue();
        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getThumbnailUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()
                )).toList();
    }

    @Override
    public List<PaperResponse> getAllPapersIsPending() {
        List<Paper> papers = paperRepository.findByIsApprovedFalse();
        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getThumbnailUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()
                )).toList();
    }

    @Override
    public PaperResponse getPaperById(String Uuid) {
        Paper paper = paperRepository.findByUuid(Uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));
        return new PaperResponse(
                paper.getUuid(),
                paper.getTitle(),
                paper.getAbstractText(),
                paper.getFileUrl(),
                paper.getThumbnailUrl(),
                paper.getAuthor().getUuid(),
                List.of(paper.getCategory().getName()),
                paper.getStatus(),
                paper.getIsApproved(),
                paper.getSubmittedAt(),
                paper.getCreatedAt(),
                paper.getIsPublished(),
                paper.getPublishedAt());
    }

    @Override
    public List<PaperResponse> getPapersByAuthor() {
        CurrentUser subId = userService.getCurrentUserSub();
        List<Paper> papers = paperRepository.findByAuthor_UuidAndIsDeletedFalse(subId.id());

        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getThumbnailUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()
                )).toList();
    }

    @Override
    public List<PaperResponse> getAllPaper() {
        List<Paper> papers = paperRepository.findAll();
        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getThumbnailUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()
                )).toList();
    }

    @Override
    public void deletePaperById(String uuid) {
        Paper paper = paperRepository.findByUuid(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));
        paper.setIsDeleted(true);
        paperRepository.save(paper);
    }
}

