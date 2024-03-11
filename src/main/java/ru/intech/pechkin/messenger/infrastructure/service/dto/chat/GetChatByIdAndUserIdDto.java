package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class GetChatByIdAndUserIdDto {
    private final UUID chatId;
    private final UUID userId;
}
