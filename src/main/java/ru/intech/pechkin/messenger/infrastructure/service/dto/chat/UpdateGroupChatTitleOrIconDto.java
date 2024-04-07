package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateGroupChatTitleOrIconDto {
    @NotNull
    private final UUID chatId;
    @NotNull
    private final String value;
}
