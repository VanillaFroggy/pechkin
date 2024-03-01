package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.MessageData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MessageDto {
    private UUID id;
    private UUID chatId;
    private MessagePublisherDto publisher;
    private List<MessageData> datas;
    private MessageDto relatesTo;
    private LocalDateTime dateTime;
    private Boolean checked;
    private Boolean edited;
}
