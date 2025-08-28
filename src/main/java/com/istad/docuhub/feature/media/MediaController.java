package com.istad.docuhub.feature.media;


import com.istad.docuhub.utils.FileServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

@RestController
@RequiredArgsConstructor
    @RequestMapping("/api/v1/media")
@CrossOrigin("*")
public class MediaController {

    private final MediaService mediaService;

    @PostMapping
    public ResponseEntity<?> uploadMedia(@RequestParam("file") MultipartFile file) {
        return new ResponseEntity<>(
                Map.of(
                        "message", "Media is upload successfully",
                        "data", mediaService.uploadMedia(file)
                ), HttpStatus.OK
        );
    }

    @GetMapping("/{file}")
    public ResponseEntity<?> getMedia(@PathVariable String file) {

        InputStream inputStream = mediaService.getMediaUrl(file);

        InputStreamResource resource = new InputStreamResource(inputStream);

        String contentType = FileServiceUtil.guessContentType(file);

        if (contentType == null) {
            contentType = "app lication/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file + "\"")
                .body(resource);
    }

    @DeleteMapping("/{file}")
    public ResponseEntity<?> deleteMedia(@PathVariable String file) {
        mediaService.deleteMedia(file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
