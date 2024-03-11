package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.ChatType;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserWithRoleDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChatDto {
    private UUID id;
    private ChatType chatType;
    private String title;
    private String icon;
    private List<UserWithRoleDto> usersWithRole;
    private MessageDto message;
    private long unreadMessagesCount;
    private Boolean muted;
    private Boolean pinned;
    private Boolean corporate;
}
