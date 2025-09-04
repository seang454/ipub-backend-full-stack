package com.istad.docuhub.feature.media;


import com.istad.docuhub.feature.media.dto.MediaResponse;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;

import static com.istad.docuhub.utils.FileServiceUtil.generateFileName;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    public MediaResponse uploadMedia(MultipartFile file) {

        try {
            String fileName = generateFileName(file.getOriginalFilename());

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            String uri = String.format("%s/%s/%s",endpoint, bucketName, fileName);

            return new MediaResponse(
                    fileName, uri, file.getSize(), LocalDate.now()
            );

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public InputStream getMediaUrl(String fileName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());

            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteMedia(String fileName) {

        String fileUrl = fileName.substring(fileName.lastIndexOf("/") + 1);

        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName
                    )
                    .object(fileUrl)
                    .build()
            );

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }
}
