package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateChatMutedOrPinnedStatusDto {
    @NotNull
    private UUID userId;

    @NotNull
    private UUID chatId;

    @NotNull
    private Boolean status;
}
