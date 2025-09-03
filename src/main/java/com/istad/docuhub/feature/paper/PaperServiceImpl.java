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
        // Validation
        if (paperRequest.title() == null || paperRequest.title().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be null or empty");
        }

        if (paperRequest.abstractText() == null || paperRequest.abstractText().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Abstract text cannot be null or empty");
        }

        if (paperRequest.fileUrl() == null || paperRequest.fileUrl().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be null or empty");
        }

        if (paperRequest.categoryNames() == null || paperRequest.categoryNames().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category names cannot be null or empty");
        }

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
        Paper paper = Paper.builder().id(id).uuid(UUID.randomUUID().toString()).title(paperRequest.title()).abstractText(paperRequest.abstractText()).fileUrl(paperRequest.fileUrl()).author(author).category(category).status("PENDING").submittedAt(LocalDate.now()).createdAt(LocalDate.now()).downloadCount(0).isApproved(false).isDeleted(false).isPublished(false).build();

        paperRepository.save(paper);
    }

    @Override
    public List<PaperResponse> getAllPapers() {
        List<Paper> papers = paperRepository.findByIsDeletedIsFalseAndIsApprovedTrueAndIsPublishedIsTrue();

        return papers.stream().map(paper -> new PaperResponse(paper.getUuid(), paper.getTitle(), paper.getAbstractText(), paper.getFileUrl(), paper.getAuthor().getUuid(), List.of(paper.getCategory().getName()), paper.getStatus(), paper.getIsApproved(), paper.getSubmittedAt(), paper.getCreatedAt(), paper.getIsPublished(), paper.getPublishedAt()

        )).toList(); // Java 16+ (use .collect(Collectors.toList()) if < Java 16)
    }

    @Override
    public List<PaperResponse> getAllPapersIsPending() {
        List<Paper> papers = paperRepository.findByIsApprovedFalse();
        return papers.stream().map(paper -> new PaperResponse(paper.getUuid(), paper.getTitle(), paper.getAbstractText(), paper.getFileUrl(), paper.getAuthor().getUuid(), List.of(paper.getCategory().getName()), paper.getStatus(), paper.getIsApproved(), paper.getSubmittedAt(), paper.getCreatedAt(), paper.getIsPublished(), paper.getPublishedAt())).toList();
    }

    @Override
    public PaperResponse getPaperById(String Uuid) {
        Paper paper = paperRepository.findByUuid(Uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));
        return new PaperResponse(paper.getUuid(), paper.getTitle(), paper.getAbstractText(), paper.getFileUrl(), paper.getAuthor().getUuid(), List.of(paper.getCategory().getName()), paper.getStatus(), paper.getIsApproved(), paper.getSubmittedAt(), paper.getCreatedAt(), paper.getIsPublished(), paper.getPublishedAt());
    }

    @Override
    public List<PaperResponse> getPapersByAuthor() {
        CurrentUser subId = userService.getCurrentUserSub();
        List<Paper> paper = paperRepository.findByAuthor_UuidAndIsDeletedFalse(subId.id());

        return paper.stream().map(paper1 -> new PaperResponse(paper1.getUuid(), paper1.getTitle(), paper1.getAbstractText(), paper1.getFileUrl(), paper1.getAuthor().getUuid(), List.of(paper1.getCategory().getName()), paper1.getStatus(), paper1.getIsApproved(), paper1.getSubmittedAt(), paper1.getCreatedAt(), paper1.getIsPublished(), paper1.getPublishedAt())).toList();
    }
}

