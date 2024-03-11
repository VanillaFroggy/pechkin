package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReplyToMessageDto {
    private UUID chatId;

    private UUID userId;

    private UUID messageToReplyId;

    @Min(1)
    private List<MessageDataDto> dataDtos;
}
