package com.istad.docuhub.feature.paper;

import com.istad.docuhub.feature.paper.dto.PaperRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<?> getAllPapersIsPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapersIsPublished(pageable),
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
    public ResponseEntity<?> getPapersByAuthor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getPapersByAuthor(pageable),
                        "message", "Papers by author retrieved successfully"
                ), HttpStatus.OK
        );
    }

    @GetMapping("/author/approved")
    public ResponseEntity<?> getAllPapersIsApprovedForAuthor(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return new ResponseEntity<>(
                Map.of(
                        "papers", paperService.getAllPapersIsApprovedForAuthor(pageable),
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

    @PostMapping("/publish/{uuid}")
    public ResponseEntity<?> publishPaperByUuid(@PathVariable String uuid) {
        paperService.publishPaperByPaperUuid(uuid);
        return ResponseEntity.status(HttpStatus.OK).body("Paper published successfully");
    }

    @GetMapping("/author/papers/stars")
    public ResponseEntity<?> getAllDownloadCountOfPapers(){
        return ResponseEntity.ok(paperService.getAllStarOfPapers());
    }

    @PostMapping("/download/{paperUuid}")
    public ResponseEntity<?> addPaperDownloadCount(@PathVariable String paperUuid){
        paperService.addPaperDownloadCount(paperUuid);
        return ResponseEntity.ok("Download count updated successfully");
    }
}
