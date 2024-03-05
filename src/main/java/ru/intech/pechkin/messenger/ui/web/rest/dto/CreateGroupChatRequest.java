package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateGroupChatRequest {
    private String title;
    private String icon;
    private Map<UUID, Role> users;
}
