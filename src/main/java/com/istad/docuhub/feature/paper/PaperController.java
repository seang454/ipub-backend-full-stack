package com.istad.docuhub.feature.paper;

import com.istad.docuhub.feature.paper.dto.PaperRequest;
import com.istad.docuhub.feature.paper.dto.PaperResponse;
import jakarta.ws.rs.PUT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/papers")
@RequiredArgsConstructor
public class PaperController {
    private final PaperService paperService;

    @PostMapping
    public ResponseEntity<?> createPaper(@RequestBody PaperRequest paperRequest) {

        paperService.createPaper(paperRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Paper created successfully");
    }

    @GetMapping("/published")
    public ResponseEntity<?> getAllPapersIsPublished() {
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapersIsPublished(),
                        "message", "All published papers retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getPaperById(@PathVariable String uuid) {
        return new ResponseEntity<>(
                Map.of(
                        "paper", paperService.getPaperById(uuid),
                        "message", "Paper retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/author")
    public ResponseEntity<?> getPapersByAuthor() {
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getPapersByAuthor(),
                        "message", "Papers by author retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/author/approved")
    public ResponseEntity<?> getAllPapersIsApprovedForAuthor() {
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapersIsApprovedForAuthor(),
                        "message", "Approved papers by author retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @DeleteMapping("/author/{uuid}")
    public ResponseEntity<?> deletePaperByAuthor(@PathVariable String uuid) {
        paperService.deletePaperByAuthor(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Paper deleted successfully");
    }

    @PutMapping("/author/{uuid}")
    public ResponseEntity<?> updatePaperByAuthor(@PathVariable String uuid, @RequestBody PaperRequest paperRequest) {
        return new ResponseEntity<>(
                Map.of(
                        "paper", paperService.updatePaperByAuthor(uuid, paperRequest),
                        "message", "Paper updated successfully"
                ), HttpStatus.OK
        );
    }
}
