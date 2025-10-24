package com.istad.docuhub.feature.star;

import com.istad.docuhub.feature.star.dto.StarResponse;
import com.istad.docuhub.feature.user.dto.UserPublicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/stars")
@RequiredArgsConstructor
public class StarController {

    private final StarService starService;

    // Star a paper
    @PostMapping("/{paperUuid}")
    public ResponseEntity<StarResponse> starPaper(@PathVariable String paperUuid) {
        StarResponse response = starService.starReaction(paperUuid);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Unstar a paper
    @DeleteMapping("/{paperUuid}")
    public ResponseEntity<StarResponse> unstarPaper(@PathVariable String paperUuid) {
        StarResponse response = starService.unstarReaction(paperUuid);
        return ResponseEntity.ok(response);
    }

    // Get star count
    @GetMapping("/{paperUuid}/count")
    public ResponseEntity<Long> countStars(@PathVariable String paperUuid) {
        long count = starService.countByPaperUuid(paperUuid);
        return ResponseEntity.ok(count);
    }

    // Check if user has starred a paper
    @GetMapping("/{paperUuid}/user/{userUuid}")
    public ResponseEntity<Map<String, Object>> hasUserStarred(
            @PathVariable String paperUuid,
            @PathVariable String userUuid) {
        boolean hasStarred = starService.hasUserStarredPaper(paperUuid, userUuid);
        return ResponseEntity.ok(Map.of(
                "paperUuid", paperUuid,
                "userUuid", userUuid,
                "hasStarred", hasStarred
        ));
    }

    // Get all users who starred
    @GetMapping("/{paperUuid}/users")
    public ResponseEntity<List<UserPublicResponse>> getUsersWhoStarred(@PathVariable String paperUuid) {
        List<UserPublicResponse> users = starService.getUsersByPaperUuid(paperUuid);
        return ResponseEntity.ok(users);
    }

    // Get all stars by user
    @GetMapping("/user/{userUuid}")
    public ResponseEntity<List<StarResponse>> getAllStarsByUserUuid(@PathVariable String userUuid) {
        List<StarResponse> stars = starService.getAllStarsByUserUuid(userUuid);
        return ResponseEntity.ok(stars);
    }
}