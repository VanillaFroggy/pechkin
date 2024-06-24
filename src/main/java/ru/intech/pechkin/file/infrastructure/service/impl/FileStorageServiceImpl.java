package ru.intech.pechkin.file.infrastructure.service.impl;

import io.minio.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.infrastructure.persistence.entity.FileMetadata;
import ru.intech.pechkin.file.infrastructure.persistence.repo.FileMetadataRepository;
import ru.intech.pechkin.file.infrastructure.service.FileStorageService;
import ru.intech.pechkin.file.infrastructure.service.dto.DownloadingFileResponse;
import ru.intech.pechkin.file.infrastructure.service.dto.UploadingFileResponse;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final MinioClient minioClient;
    private final FileMetadataRepository fileMetadataRepository;

    @Value("${spring.minio.bucket}")
    private String bucket;

    @Override
    @SneakyThrows
    public UploadingFileResponse uploadFile(@NotNull String folder, @NotNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String hash = DigestUtils.sha256Hex(file.getInputStream());
        Optional<FileMetadata> existingFile = fileMetadataRepository.findByHash(hash);
        if (existingFile.isPresent()) {
            return new UploadingFileResponse(existingFile.get().getId());
        }
        Path filePath = File.createTempFile("temp", null).toPath();
        file.transferTo(filePath);
        String objectKey = minioClient.uploadObject(
                UploadObjectArgs.builder()
                        .bucket(bucket)
                        .object(folder + "/" + System.currentTimeMillis()
                                + "_" + file.getOriginalFilename())
                        .filename(filePath.toString())
                        .build()
        ).object();
        fileMetadataRepository.save(new FileMetadata(objectKey, hash, file.getContentType()));
        return new UploadingFileResponse(objectKey);
    }

    @Override
    @SneakyThrows
    public DownloadingFileResponse downloadFile(@NotNull String objectKey) {
        return new DownloadingFileResponse(
                fileMetadataRepository.findById(objectKey)
                        .orElseThrow(NullPointerException::new)
                        .getContentType(),
                new InputStreamResource(
                        minioClient.getObject(
                                GetObjectArgs.builder()
                                        .bucket(bucket)
                                        .object(objectKey)
                                        .build()
                        )
                )
        );
    }

    @Override
    @SneakyThrows
    public void deleteFile(@NotNull String objectKey) {
        fileMetadataRepository.deleteById(objectKey);
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
