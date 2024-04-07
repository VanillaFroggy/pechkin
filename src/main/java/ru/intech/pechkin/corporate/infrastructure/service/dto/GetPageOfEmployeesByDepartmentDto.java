package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfEmployeesByDepartmentDto {
    private final UUID departmentId;

    @Min(0)
    private final int pageNumber;

    @Min(1)
    @Max(50)
    private final int pageSize;
}
