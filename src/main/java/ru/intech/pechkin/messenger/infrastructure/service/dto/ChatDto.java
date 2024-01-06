package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.ChatType;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Message;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChatDto {
    private UUID id;
    private ChatType chatType;
    private String title;
    private List<UserWithRoleDto> usersWithRole;
    private Message message;
}
