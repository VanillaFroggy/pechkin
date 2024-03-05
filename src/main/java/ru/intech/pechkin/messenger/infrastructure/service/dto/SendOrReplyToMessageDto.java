package ru.intech.pechkin.messenger.infrastructure.service.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Message;

import java.util.List;
import java.util.UUID;

@Data
public class SendOrReplyToMessageDto {
    private UUID chatId;

    private UUID userId;

    private Message messageToReply;

    @Min(1)
    private List<MessageDataDto> dataDtos;
}
