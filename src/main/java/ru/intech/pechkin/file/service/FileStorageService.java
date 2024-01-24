package ru.intech.pechkin.file.service;

import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.service.dto.UploadingFileResponse;

import java.io.IOException;

public interface FileStorageService {
    UploadingFileResponse uploadFile(String folder, MultipartFile file) throws IOException;

    byte[] downloadFile(String objectKey) throws IOException;

    void deleteFile(String objectKey);
}
