package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DeleteMessageDto {
    @NotNull
    private UUID chatId;

    @NotNull
    private UUID messageId;
}
