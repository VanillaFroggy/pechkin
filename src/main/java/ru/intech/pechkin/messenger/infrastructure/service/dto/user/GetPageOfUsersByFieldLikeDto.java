package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetPageOfUsersByFieldLikeDto {
    @NotNull
    private final String value;

    @Min(0)
    private final int pageNumber;

    @Min(1)
    @Max(50)
    private final int pageSize;
}
