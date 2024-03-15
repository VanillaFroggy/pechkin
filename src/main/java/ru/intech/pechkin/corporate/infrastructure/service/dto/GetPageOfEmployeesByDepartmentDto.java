package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfEmployeesByDepartmentDto {
    private final UUID departmentId;

    @Size
    private final int pageNumber;

    @Size(min = 1, max = 50)
    private final int pageSize;
}
