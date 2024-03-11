package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GetPageOfDepartmentsDto {
    @Size()
    private final int pageNumber;

    @Size(min = 1, max = 100)
    private final int pageSize;
}
