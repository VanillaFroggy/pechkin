package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateChatMutedStatusDto {
    private UUID userId;
    private UUID chatId;
    private Boolean muted;
}
