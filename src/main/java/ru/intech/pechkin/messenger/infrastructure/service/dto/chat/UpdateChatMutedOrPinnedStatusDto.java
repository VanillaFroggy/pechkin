package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateChatMutedOrPinnedStatusDto {
    private UUID userId;
    private UUID chatId;
    private Boolean status;
}
