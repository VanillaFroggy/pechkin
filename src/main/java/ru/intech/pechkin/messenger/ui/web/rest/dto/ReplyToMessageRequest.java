package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageDataDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ReplyToMessageRequest {
    private UUID chatId;
    private UUID userId;
    private UUID messageToReplyId;
    private List<MessageDataDto> dataDtos;
    private LocalDateTime dateTime;
}

