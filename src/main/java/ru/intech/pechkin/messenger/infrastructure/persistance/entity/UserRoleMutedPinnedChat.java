package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document("userRoleMutedPinnedChats")
public class UserRoleMutedPinnedChat {
    @Id
    UUID id;
    UUID chatId;
    UUID userId;
    Role userRole;
    Boolean muted;
    Boolean pinned;

    public static UserRoleMutedPinnedChat create(UUID userId, UUID chatId, Role userRole) {
        return UserRoleMutedPinnedChat.builder()
                .id(UUID.randomUUID())
                .chatId(chatId)
                .userId(userId)
                .userRole(userRole)
                .muted(false)
                .pinned(false)
                .build();
    }
}
