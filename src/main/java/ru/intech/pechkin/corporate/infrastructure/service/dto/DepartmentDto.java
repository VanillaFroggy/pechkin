package ru.intech.pechkin.corporate.infrastructure.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DepartmentDto {
    private UUID id;
    private String title;
    private UUID parent;
}
