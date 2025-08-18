package com.istad.docuhub.feature.media;

import com.istad.docuhub.feature.media.dto.MediaResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MediaService {

    MediaResponse uploadMedia(MultipartFile file);
    InputStream getMediaUrl(String fileName);
    void deleteMedia(String fileName);
}
