package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class GetChatByIdAndUserIdDto {
    private final UUID chatId;
    private final UUID userId;
}
