package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Message;

import java.util.List;
import java.util.UUID;

@Data
public class SendOrReplyToMessageDto {
    private UUID chatId;
    private UUID userId;
    private Message messageToReply;
    private List<MessageDataDto> dataDtos;
}
