package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document("userRoleChats")
public class UserRoleChat {
    @Id
    UUID id;
    UUID chatId;
    UUID userId;
    Role userRole;
}
