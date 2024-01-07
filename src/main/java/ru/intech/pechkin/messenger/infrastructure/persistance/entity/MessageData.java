package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@Document("messagesData")
public class MessageData {
    @Id
    private UUID id;
    private MessageType messageType;
    private String value;
}
