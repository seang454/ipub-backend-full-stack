package com.istad.docuhub.feature.paper;


import com.istad.docuhub.domain.Category;
import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.category.CategoryRepository;
import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import com.istad.docuhub.feature.user.UserRepository;
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
        User author = userRepository.findByUuidAndIsDeletedFalse(paperRequest.authorUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

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
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Category not found: " + categoryName));

        // Create paper with auto-generated ID and category UUID
        Paper paper = Paper.builder()
                .id(id)
                .uuid(UUID.randomUUID().toString())
                .title(paperRequest.title())
                .abstractText(paperRequest.abstractText())
                .fileUrl(paperRequest.fileUrl())
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
    public List<PaperResponse> getAllPapers() {
        List<Paper> papers = paperRepository.findByIsDeletedIsFalseAndIsApprovedTrueAndIsPublishedIsTrue();

        return papers.stream()
                .map(paper -> new PaperResponse(
                        paper.getUuid(),
                        paper.getTitle(),
                        paper.getAbstractText(),
                        paper.getFileUrl(),
                        paper.getAuthor().getUuid(),
                        List.of(paper.getCategory().getName()),
                        paper.getStatus(),
                        paper.getIsApproved(),
                        paper.getSubmittedAt(),
                        paper.getCreatedAt(),
                        paper.getIsPublished(),
                        paper.getPublishedAt()

                ))
                .toList(); // Java 16+ (use .collect(Collectors.toList()) if < Java 16)
    }

}

