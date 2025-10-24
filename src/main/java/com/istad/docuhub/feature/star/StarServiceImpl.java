package com.istad.docuhub.feature.star;

import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.Star;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import com.istad.docuhub.feature.user.dto.UserPublicResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StarServiceImpl implements StarService {

    private final UserService userService;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final StarRepository starRepository;

    @Override
    @Transactional
    public StarResponse starReaction(String paperUuid) {
        // Get User ID by token
        CurrentUser userId = userService.getCurrentUserSub();

        // Get paper by uuid
        Paper paper = paperRepository.findPaperByUuidAndIsApprovedTrueAndIsPublishedTrueAndIsDeletedFalse(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper Not Found"));

        // Get user by uuid
        User user = userRepository.findByUuidAndIsDeletedFalse(userId.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found"));

        // Check if already starred or not
        if (starRepository.existsByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already starred this paper");
        }

        // Create new star
        Star star = createNewStar(paper, user);
        starRepository.save(star);

        // Get updated star count
        long starCount = starRepository.countByPaper_Uuid(paperUuid);

        return StarResponse.starred(paperUuid, user.getUuid(), starCount);
    }

    @Override
    @Transactional
    public StarResponse unstarReaction(String paperUuid) {
        // Get User ID
        CurrentUser userId = userService.getCurrentUserSub();

        // Find paper
        Paper paper = paperRepository.findPaperByUuidAndIsApprovedTrueAndIsPublishedTrueAndIsDeletedFalse(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        // Get User
        User user = userRepository.findByUuidAndIsDeletedFalse(userId.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Check if this user starred the paper
        if (!starRepository.existsByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Star not found for this user on this paper");
        }

        // Delete the star
        starRepository.deleteByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid());

        // Get updated star count
        long starCount = starRepository.countByPaper_Uuid(paperUuid);

        return StarResponse.unstarred(paperUuid, user.getUuid(), starCount);
    }

    @Override
    public long countByPaperUuid(String paperUuid) {
        // Validate paper exists
        Paper paper = paperRepository.findByUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));
        return starRepository.countByPaper_Uuid(paper.getUuid());
    }

    @Override
    public boolean hasUserStarredPaper(String paperUuid, String userUuid) {
        // Validate paper exists
        paperRepository.findByUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        // Validate user exists
        userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return starRepository.existsByPaper_UuidAndUser_Uuid(paperUuid, userUuid);
    }

    @Override
    public List<UserPublicResponse> getUsersByPaperUuid(String paperUuid) {
        // Validate paper exists
        Paper paper = paperRepository.findByUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper not found"));

        List<Star> stars = starRepository.findByPaper_Uuid(paperUuid);

        return stars.stream().map(star -> {
            User user = star.getUser();
            return new UserPublicResponse(
                    user.getUuid(),
                    user.getSlug(),
                    user.getGender(),
                    user.getFullName(),
                    user.getImageUrl(),
                    user.getStatus(),
                    user.getCreateDate(),
                    user.getUpdateDate(),
                    user.getBio(),
                    user.getIsUser(),
                    user.getIsAdmin(),
                    user.getIsAdvisor(),
                    user.getIsStudent()
            );
        }).toList();
    }

    @Override
    public List<StarResponse> getAllStarsByUserUuid(String userUuid) {
        // Validate user exists
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Star> stars = starRepository.findStarByUser_Uuid(user.getUuid());

        return stars.stream().map(star -> {
            long starCount = starRepository.countByPaper_Uuid(star.getPaper().getUuid());
            return StarResponse.builder()
                    .paperUuid(star.getPaper().getUuid())
                    .userUuid(star.getUser().getUuid())
                    .starred(true)
                    .message("Starred paper")
                    .starCount(starCount)
                    .build();
        }).toList();
    }

    private Star createNewStar(Paper paper, User user) {
        Star star = new Star();

        // Set ID
        Integer id;
        do {
            id = (int) (Math.random() * 1_000_000);
        } while (starRepository.existsById(id));
        star.setId(id);

        // Set UUID
        String starUuid;
        do {
            starUuid = UUID.randomUUID().toString();
        } while (starRepository.existsByUuid(starUuid));
        star.setUuid(starUuid);

        star.setPaper(paper);
        star.setUser(user);
        star.setStaredAt(LocalDate.now());

        return star;
    }
}