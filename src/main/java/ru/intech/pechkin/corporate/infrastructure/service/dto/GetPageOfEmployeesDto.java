package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GetPageOfEmployeesDto {
    @Size
    private final int pageNumber;

    @Size(min = 1, max = 50)
    private final int pageSize;
}
