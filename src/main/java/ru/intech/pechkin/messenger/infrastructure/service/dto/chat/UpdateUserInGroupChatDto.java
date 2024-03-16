package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.UUID;

@Data
public class UpdateUserInGroupChatDto {
    private final UUID chatId;
    private final UUID userId;
    private final Role userRole;
}
