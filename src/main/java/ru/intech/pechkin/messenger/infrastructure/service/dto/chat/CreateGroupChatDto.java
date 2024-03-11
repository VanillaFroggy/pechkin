package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CreateGroupChatDto {
    private final UUID departmentId;
    private final String title;
    private final String icon;
    private final Map<UUID, Role> users;
    private final Boolean corporate;
}
