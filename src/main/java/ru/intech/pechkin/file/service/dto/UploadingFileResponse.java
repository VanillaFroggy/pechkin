package ru.intech.pechkin.file.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UploadingFileResponse {
    private final String fileUrl;
}
