package com.istad.docuhub.feature.star;


import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.star.dto.StarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> unstarPaper(@PathVariable String paperUuid) {
        starService.unstarReaction(paperUuid);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT
    }

    // Get star count
    @GetMapping("/{paperUuid}/count")
    public ResponseEntity<Long> countStars(@PathVariable String paperUuid) {
        long count = starService.countByPaperUuid(paperUuid);
        return ResponseEntity.ok(count);
    }

    // Get all users who starred
    @GetMapping("/{paperUuid}/users")
    public ResponseEntity<List<User>> getUsersWhoStarred(@PathVariable String paperUuid) {
        List<User> users = starService.getUsersByPaperUuid(paperUuid);
        return ResponseEntity.ok(users);
    }


}
