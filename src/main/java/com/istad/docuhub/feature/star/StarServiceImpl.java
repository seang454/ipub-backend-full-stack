package com.istad.docuhub.feature.star;

import com.istad.docuhub.domain.Paper;
import com.istad.docuhub.domain.Star;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.comment.CurrentUserUtil;
import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class StarServiceImpl implements StarService {


    private PaperRepository paperRepository;
    private UserRepository userRepository;
    private StarRepository starRepository;
    private CurrentUserUtil currentUserUtil;


    @Override
    public StarResponse starReaction(String paperUuid) {

        Integer userId = currentUserUtil.getCurrentUserId();

        Paper paper = paperRepository.findByUuid(paperUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Paper Not Found")
        );

        if(!paper.getIsPublished()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Paper Not Published");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found")
        );

        if (starRepository.existsByPaper_UuidAndUser_Uuid(paperUuid, user.getUuid())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already starred this paper");
        }

        Star star = new Star();
        star.setPaper(paper);
        star.setUser(user);
        star.setStaredAt(LocalDate.now());

        String starUuid;
        do {
            starUuid = UUID.randomUUID().toString();
        } while (starRepository.existsByUuid(starUuid));

        star.setUuid(starUuid);
        starRepository.save(star);

        return StarResponse.builder()
                .userId(userId)
                .userUuid(user.getUuid())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .starredAt(star.getStaredAt())
                .build();
    }


    @Override
    public void unstarReaction(String paperUuid) {

        // Find paper
        Paper paper = paperRepository.findByUuid(paperUuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Paper not found")
        );

        // Find user
        Integer userId = currentUserUtil.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(
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
        return starRepository.countByPaper_Uuid(paperUuid);
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
