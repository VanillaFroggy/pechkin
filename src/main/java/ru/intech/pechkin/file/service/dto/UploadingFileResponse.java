package ru.intech.pechkin.file.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadingFileResponse {
    private String fileUrl;
}
