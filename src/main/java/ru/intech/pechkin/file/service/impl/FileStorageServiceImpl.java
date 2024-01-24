package ru.intech.pechkin.file.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.service.FileStorageService;
import ru.intech.pechkin.file.service.dto.UploadingFileResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String fileStorageDirectory = "file-storage/";

    @Override
    public UploadingFileResponse uploadFile(String folder, MultipartFile file) throws IOException {
        File filePath = new File(fileStorageDirectory + folder);
        if (!filePath.exists()) {
            filePath.mkdir();
        }
        Path path = Paths.get(
                filePath.getPath(),
                System.currentTimeMillis() + "_" + file.getOriginalFilename()
        );
        try (OutputStream os = Files.newOutputStream(path)) {
            os.write(file.getBytes());
        }
        return new UploadingFileResponse(
                path.toString()
                        .replaceAll("\\\\", "/")
        );
    }

    @Override
    public byte[] downloadFile(String objectKey) throws IOException {
        Path path = Paths.get(
                fileStorageDirectory,
                objectKey
        );
        byte[] fileData;
        try (InputStream is = Files.newInputStream(path)) {
            fileData = is.readAllBytes();
        }
        return fileData;
    }

    @Override
    public void deleteFile(String objectKey) {
        File file = new File(fileStorageDirectory + objectKey);
        if (!file.delete()) {
            throw new NullPointerException();
        }
    }
}
