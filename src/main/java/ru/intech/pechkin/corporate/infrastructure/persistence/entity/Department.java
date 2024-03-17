package ru.intech.pechkin.corporate.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document("departments")
@AllArgsConstructor
public class Department {
    @Id
    private UUID id;
    private String title;
    private UUID parent;
}
