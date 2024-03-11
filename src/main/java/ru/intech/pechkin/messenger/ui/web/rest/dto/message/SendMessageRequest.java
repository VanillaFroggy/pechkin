package ru.intech.pechkin.messenger.ui.web.rest.dto.message;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDataDto;

import java.util.List;
import java.util.UUID;

@Data
public class SendMessageRequest {
    private UUID chatId;
    private UUID userId;
    private List<MessageDataDto> dataDtos;
}
