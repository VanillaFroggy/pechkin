package ru.intech.pechkin.file.infrastructure.persistence.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("filesMetadata")
@RequiredArgsConstructor
public class FileMetadata {
    @Id
    private final String id;
    private final String hash;
    private final String contentType;
}
