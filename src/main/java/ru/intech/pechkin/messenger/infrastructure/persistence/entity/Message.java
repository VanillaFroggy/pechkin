package ru.intech.pechkin.messenger.infrastructure.persistence.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document("messages")
public class Message {
    @Id
    private UUID id;

    private UUID chatId;

    private UUID publisher;

    private List<MessageData> datas;

    private Message relatesTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateTime;

    private Boolean edited;
}
