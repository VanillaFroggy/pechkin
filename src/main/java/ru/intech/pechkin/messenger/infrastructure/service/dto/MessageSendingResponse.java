package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.MessageData;

import java.util.List;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class MessageSendingResponse {
    private final UUID messageId;
    private final List<MessageData> datas;
}
