package ru.intech.pechkin.file.service.impl;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.service.FileStorageService;
import ru.intech.pechkin.file.service.dto.UploadingFileResponse;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public UploadingFileResponse uploadFile(String folder, MultipartFile file) {
        Path filePath = File.createTempFile("temp", null).toPath();
        file.transferTo(filePath);
        return new UploadingFileResponse(
                minioClient.uploadObject(
                        UploadObjectArgs.builder()
                                .bucket(bucket)
                                .object(folder + "/" + System.currentTimeMillis()
                                        + "_" + file.getOriginalFilename())
                                .filename(filePath.toString())
                                .build()
                ).object()
        );
    }

    @Override
    @SneakyThrows
    public byte[] downloadFile(String objectKey) {
        byte[] fileData;
        try (InputStream is = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build())) {
            fileData = is.readAllBytes();
        }
        return fileData;
    }

    @Override
    @SneakyThrows
    public void deleteFile(String objectKey) {
        minioClient.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        );
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        );
    }
}
