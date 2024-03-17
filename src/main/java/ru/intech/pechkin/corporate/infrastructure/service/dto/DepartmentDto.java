package ru.intech.pechkin.corporate.infrastructure.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class DepartmentDto {
    private UUID id;
    private String title;
    private UUID parent;
}
