package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
public class UpdateGroupChatDto {
    private UUID chatId;
    private String title;
    private Map<UUID, Role> users;
}
