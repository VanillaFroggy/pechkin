package ru.intech.pechkin.file.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.infrastructure.service.FileStorageService;
import ru.intech.pechkin.file.infrastructure.service.dto.DownloadingFileResponse;
import ru.intech.pechkin.file.infrastructure.service.dto.UploadingFileResponse;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/file")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @PostMapping(value = "/upload/{folder}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UploadingFileResponse> upload(
            @PathVariable("folder") String folder,
            @RequestPart MultipartFile file
    ) throws IOException {
        return new ResponseEntity<>(
                fileStorageService.uploadFile(folder, file),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam String objectKey) throws IOException {
        DownloadingFileResponse response = fileStorageService.downloadFile(objectKey);
        return ResponseEntity.ok()
                .header("Content-type", response.getContentType())
                .header("Content-disposition", "attachment; fileName=\"" + objectKey + "\"")
                .body(response.getResource());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String objectKey) {
        fileStorageService.deleteFile(objectKey);
        return ResponseEntity.noContent().build();
    }
}
