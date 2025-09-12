package com.istad.docuhub.feature.specialize;

import com.istad.docuhub.feature.specialize.dto.SpecializeRequest;
import com.istad.docuhub.feature.specialize.dto.SpecializeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/specializes")
@RequiredArgsConstructor
public class SpecializeController {

    private final SpecializeService specializeService;

    @PostMapping
    public ResponseEntity<SpecializeResponse> createSpecialize(@RequestBody SpecializeRequest request) {
        return ResponseEntity.ok(specializeService.createSpecialize(request));
    }

    @GetMapping
    public ResponseEntity<List<SpecializeResponse>> getAllSpecializes() {
        return ResponseEntity.ok(specializeService.getAllSpecializes());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<SpecializeResponse> getSpecializeByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(specializeService.getSpecializeByUuid(uuid));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<SpecializeResponse> updateSpecialize(
            @PathVariable String uuid,
            @RequestBody SpecializeRequest request) {
        return ResponseEntity.ok(specializeService.updateSpecialize(uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteSpecialize(@PathVariable String uuid) {
        specializeService.deleteSpecialize(uuid);
        return ResponseEntity.noContent().build();
    }
}

