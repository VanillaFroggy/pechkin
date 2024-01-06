package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateGroupChatRequest {
    private String title;
    private Map<UUID, Role> users;
}
