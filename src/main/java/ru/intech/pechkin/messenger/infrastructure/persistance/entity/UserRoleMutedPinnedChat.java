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
}
