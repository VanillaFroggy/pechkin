package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.ChatType;

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
    private Long unreadMessagesCount;
    private Boolean muted;
    private Boolean pinned;
}
