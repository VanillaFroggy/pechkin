package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateGroupChatTitleOrIconDto {
    private final UUID chatId;
    private final String value;
}
