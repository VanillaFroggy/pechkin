package ru.intech.pechkin.file.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@RequiredArgsConstructor
public class DownloadingFileResponse {
    private final String contentType;
    private final InputStreamResource resource;
}
