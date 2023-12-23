package ru.intech.pechkin.messenger.infrastructure.persistance.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@RequiredArgsConstructor
@Document("data")
public class MessageData {
    private MessageType messageType;
    private String value;
}
