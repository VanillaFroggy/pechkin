package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class FindMessagesByValueDto {
    @NotNull
    private UUID chatId;

    @NotNull
    private UUID userId;

    @NotNull
    private String value;

    @Min(0)
    private int pageNumber;

    @Min(1)
    @Max(50)
    private int pageSize;
}
