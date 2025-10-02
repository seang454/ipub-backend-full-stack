package com.istad.docuhub.feature.star;

import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.Star;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StarServiceImpl implements StarService {

    private final UserService userService;
    private final PaperRepository paperRepository;
    private final UserRepository userRepository;
    private final StarRepository starRepository;

    @Override
    public StarResponse starReaction(String paperUuid) {

        // Get User ID
        CurrentUser userId = userService.getCurrentUserSub();

        // Get paper by uuid
        Paper paper = paperRepository.findPaperByUuidAndIsApprovedTrueAndIsPublishedTrueAndIsDeletedFalse(paperUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Paper Not Found")
        );

        // Get user by uuid
        User user = userRepository.findByUuidAndIsDeletedFalse(userId.id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found")
        );

        // Check if already starred of not
        if (starRepository.existsByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already starred this paper");
        }

        Star star = new Star();

        // Set ID
        Integer id;
        do{
            id = (int) (Math.random() * 1_000_000);
        } while (starRepository.existsById(id));
        star.setId(id);

        // Set UUID
        star.setUuid(UUID.randomUUID().toString());

        star.setPaper(paper);
        star.setUser(user);
        star.setStaredAt(LocalDate.now());

        // Save
        starRepository.save(star);

        return StarResponse.builder()
                .paperUuid(paperUuid)
                .userUuid(user.getUuid())
                .build();
    }

    @Override
    @Transactional
    public void unstarReaction(String paperUuid) {

        // Find paper
        Paper paper = paperRepository.findPaperByUuidAndIsApprovedTrueAndIsPublishedTrueAndIsDeletedFalse(paperUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Paper not found")
        );

        // Get User ID
        CurrentUser userId = userService.getCurrentUserSub();

        // Get User
        User user = userRepository.findByUuidAndIsDeletedFalse(userId.id()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found")
        );

        // Check if this user starred the paper
        if(!starRepository.existsByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Star not found for this user on this paper");
        }

        // Delete the star
        starRepository.deleteByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid());
    }

    @Override
    public long countByPaperUuid(String paperUuid) {
        Paper paper = paperRepository.findByUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paper not found"
                ));
        return starRepository.countByPaper_Uuid(paper.getUuid());
    }

    @Override
    public List<User> getUsersByPaperUuid(String paperUuid) {
        Paper paper = paperRepository.findByUuid(paperUuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paper not found"
                ));
        List<Star> stars = starRepository.findByPaper_Uuid(paperUuid);
        return stars.stream()
                .map(Star::getUser)
                .toList();
    }
}
