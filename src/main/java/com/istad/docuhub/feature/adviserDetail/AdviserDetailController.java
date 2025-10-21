package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailRequest;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import com.istad.docuhub.feature.adviserDetail.dto.UpdateAdviserDetailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/adviser_details")
@RequiredArgsConstructor
public class AdviserDetailController {

    private final AdviserService adviserService;

    @PostMapping
    public ResponseEntity<AdviserDetailResponse> createAdviser(@RequestBody AdviserDetailRequest request) {
        return ResponseEntity.ok(adviserService.createAdviserDetail(request));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<AdviserDetailResponse> getAdviserByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(adviserService.getAdviserDetailByUuid(uuid));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<AdviserDetailResponse> updateAdviserDetailByUuid(
            @PathVariable String uuid,
            @RequestBody UpdateAdviserDetailRequest request) {
        return ResponseEntity.ok(adviserService.updateAdviserDetailByUuid(uuid, request));
    }


    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAdviser(@PathVariable String uuid) {
        adviserService.deleteAdviserDetail(uuid);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<AdviserDetailResponse>> getAllAdvisers() {
        return ResponseEntity.ok(adviserService.getAllAdviserDetails());
    }

    @PutMapping
    public ResponseEntity<AdviserDetailResponse>updateByToken(@RequestBody  UpdateAdviserDetailRequest request){
        return ResponseEntity.ok(adviserService.updateByToken(request));
    }

    @GetMapping("/assignments")
    public ResponseEntity<?> getAllAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        return new ResponseEntity<>(
                Map.of(
                        "status", HttpStatus.OK,
                        "data", adviserService.getAllAssignment(pageable)
                ), HttpStatus.OK
        );
    }
}
