package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateDepartmentDto {
    @NotNull
    private final String title;

    private final UUID parent;
}
