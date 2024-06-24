package ru.intech.pechkin.file.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DownloadingFileResponse {
    private String contentType;
    private byte[] content;
}
