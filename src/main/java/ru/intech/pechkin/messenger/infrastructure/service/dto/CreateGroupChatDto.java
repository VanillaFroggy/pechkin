package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
public class CreateGroupChatDto {
    private UUID departmentId;
    private String title;
    private String icon;
    private Map<UUID, Role> users;
    private Boolean corporate;
}
