package ru.intech.pechkin.messenger.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@Document("messagesData")
public class MessageData {
    @Id
    private UUID id;
    private MessageType messageType;
    private String value;
}
