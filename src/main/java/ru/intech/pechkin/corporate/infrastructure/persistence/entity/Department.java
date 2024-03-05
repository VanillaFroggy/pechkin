package ru.intech.pechkin.corporate.infrastructure.persistence.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document("departments")
@RequiredArgsConstructor
public class Department {
    @Id
    private UUID id;
    private String title;
    private UUID parent;
}
