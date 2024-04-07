package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfMessagesDto {
    @NotNull
    private final UUID chatId;

    @NotNull
    private final UUID userId;

    @Min(0)
    private final int pageNumber;

    @Min(1)
    @Max(50)
    private final int pageSize;
}
