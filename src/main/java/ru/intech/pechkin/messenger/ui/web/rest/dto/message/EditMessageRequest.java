package ru.intech.pechkin.messenger.ui.web.rest.dto.message;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageData;

import java.util.List;
import java.util.UUID;

@Data
public class EditMessageRequest {
    private UUID chatId;
    private UUID messageId;
    private List<MessageData> datas;
}
