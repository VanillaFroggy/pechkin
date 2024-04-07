package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SetMessageCheckedDto {
    @NotNull
    private UUID chatId;

    @NotNull
    private UUID userId;

    private UUID publisherId;

    @NotNull
    private UUID messageId;
}
