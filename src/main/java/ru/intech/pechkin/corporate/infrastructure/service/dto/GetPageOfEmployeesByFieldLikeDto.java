package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GetPageOfEmployeesByFieldLikeDto {
    @NotNull
    private final String value;

    @Min(0)
    private final int pageNumber;

    @Min(1)
    @Max(50)
    private final int pageSize;
}
