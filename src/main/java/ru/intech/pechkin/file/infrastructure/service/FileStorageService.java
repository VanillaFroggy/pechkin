package ru.intech.pechkin.file.infrastructure.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.infrastructure.service.dto.DownloadingFileResponse;
import ru.intech.pechkin.file.infrastructure.service.dto.UploadingFileResponse;

import java.io.IOException;

public interface FileStorageService {
    UploadingFileResponse uploadFile(@NotNull String folder, @NotNull MultipartFile file) throws IOException;

    DownloadingFileResponse downloadFile(@NotNull String objectKey) throws IOException;

    void deleteFile(@NotNull String objectKey);
}
