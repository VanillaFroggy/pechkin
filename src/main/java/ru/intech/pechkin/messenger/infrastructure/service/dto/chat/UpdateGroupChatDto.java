package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
public class UpdateGroupChatDto {
    private UUID chatId;
    private String title;
    private String icon;
    private Map<UUID, Role> users;
}
