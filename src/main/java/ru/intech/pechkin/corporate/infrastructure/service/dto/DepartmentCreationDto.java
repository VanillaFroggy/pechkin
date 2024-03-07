package ru.intech.pechkin.corporate.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class DepartmentCreationDto {
    private final UUID departmentId;
}
