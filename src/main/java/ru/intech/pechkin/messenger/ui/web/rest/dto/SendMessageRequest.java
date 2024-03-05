package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageDataDto;

import java.util.List;
import java.util.UUID;

@Data
public class SendMessageRequest {
    private UUID chatId;
    private UUID userId;
    private List<MessageDataDto> dataDtos;
}
