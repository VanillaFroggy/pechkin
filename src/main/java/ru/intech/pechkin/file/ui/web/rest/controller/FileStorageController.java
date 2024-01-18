package ru.intech.pechkin.file.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.intech.pechkin.file.service.FileStorageService;
import ru.intech.pechkin.file.service.dto.UploadingFileResponse;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/file")
@RequiredArgsConstructor
public class FileStorageController {
    private final FileStorageService fileStorageService;

    @PostMapping("/upload/{folder}")
    public ResponseEntity<UploadingFileResponse> upload(@PathVariable("folder") String folder, @RequestParam MultipartFile file) throws IOException {
        return new ResponseEntity<>(
                fileStorageService.uploadFile(folder, file),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@RequestParam String objectKey) throws IOException {
        return ResponseEntity.ok()
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; fileName=\"" + objectKey + "\"")
                .body(new ByteArrayResource(fileStorageService.downloadFile(objectKey)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String objectKey) {
        fileStorageService.deleteFile(objectKey);
        return ResponseEntity.noContent().build();
    }
}
