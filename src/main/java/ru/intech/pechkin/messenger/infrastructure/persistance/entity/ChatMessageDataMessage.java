package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Builder
@Document("messageDataMessages")
public class ChatMessageDataMessage {
    @Id
    UUID id;
    UUID chatId;
    UUID messageId;
    UUID messageDataId;

    public static ChatMessageDataMessage create(UUID chatId, UUID messageId, UUID messageDataId) {
        return ChatMessageDataMessage.builder()
                .id(UUID.randomUUID())
                .chatId(chatId)
                .messageId(messageId)
                .messageDataId(messageDataId)
                .build();
    }
}
