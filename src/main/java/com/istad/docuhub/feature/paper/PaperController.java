package com.istad.docuhub.feature.paper;

import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/papers")
@RequiredArgsConstructor
public class PaperController {
    private final PaperService paperService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createPaper(@RequestBody PaperRequest paperRequest) {

        paperService.createPaper(paperRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Paper created successfully");
    }

    @GetMapping
    public ResponseEntity<?> getAllPapers() {
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapers(),
                        "message", "All papers retrieved successfully"
                ), HttpStatus.OK
        );
    };
}
