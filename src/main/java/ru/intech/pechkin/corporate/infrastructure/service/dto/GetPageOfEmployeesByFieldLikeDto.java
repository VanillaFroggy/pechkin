package ru.intech.pechkin.corporate.infrastructure.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GetPageOfEmployeesByFieldLikeDto {
    private final String value;

    @Size
    private final int pageNumber;

    @Size(min = 1, max = 50)
    private final int pageSize;
}
