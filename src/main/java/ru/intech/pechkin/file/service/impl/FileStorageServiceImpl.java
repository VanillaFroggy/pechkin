package ru.intech.pechkin.file.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.service.FileStorageService;
import ru.intech.pechkin.file.service.dto.UploadingFileResponse;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {
    private final S3Client s3Client;
    private final String bucketName = "pechkin";

    @Override
    public UploadingFileResponse uploadFile(String  folder, MultipartFile file) throws IOException {
        String objectKey = folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType(file.getContentType())
                        .build(),
                RequestBody.fromByteBuffer(ByteBuffer.wrap(file.getBytes()))
        );
        return new UploadingFileResponse(getObjectUrl(objectKey));
    }

    @Override
    public byte[] downloadFile(String objectKey) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();
        return s3Client.getObject(getObjectRequest).readAllBytes();
    }

    @Override
    public void deleteFile(String objectKey) throws S3Exception {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build()
        );
    }

    private String getObjectUrl(String objectKey) {
        // Составить URL для доступа к загруженному файлу
        return s3Client.utilities()
                .getUrl(
                        GetUrlRequest.builder()
                                .bucket(bucketName)
                                .key(objectKey)
                                .build())
                .toExternalForm();
    }
}
