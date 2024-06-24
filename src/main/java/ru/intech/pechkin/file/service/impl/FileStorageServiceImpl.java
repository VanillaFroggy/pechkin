package ru.intech.pechkin.file.service.impl;

import io.minio.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.service.FileStorageService;
import ru.intech.pechkin.file.service.dto.DownloadingFileResponse;
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
    public UploadingFileResponse uploadFile(@NotNull String folder, @NotNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
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
    public DownloadingFileResponse downloadFile(@NotNull String objectKey) {
        DownloadingFileResponse response;
        InputStream is = InputStream.nullInputStream();
        try {
            is = getInputStream(objectKey);
            response = new DownloadingFileResponse();
            response.setContentType(new Tika().detect(is));
            is.close();
            is = getInputStream(objectKey);
            response.setContent(is.readAllBytes());
        } finally {
            is.close();
        }
        return response;
    }

    @SneakyThrows
    private InputStream getInputStream(String objectKey) {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .build()
        );
    }

    @Override
    @SneakyThrows
    public void deleteFile(@NotNull String objectKey) {
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
