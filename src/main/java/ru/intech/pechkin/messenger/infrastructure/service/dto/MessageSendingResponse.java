package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.MessageData;

import java.util.List;
import java.util.UUID;

@Data
public class MessageSendingResponse {
    private UUID messageId;
    private List<MessageData> datas;
}
