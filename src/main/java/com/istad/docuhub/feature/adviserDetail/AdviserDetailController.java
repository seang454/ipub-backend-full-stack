package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailRequest;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{uuid}")
    public ResponseEntity<AdviserDetailResponse> updateAdviser(@PathVariable String uuid,
                                                               @RequestBody AdviserDetailRequest request) {
        return ResponseEntity.ok(adviserService.updateAdviserDetail(uuid, request));
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
}
