package ru.intech.pechkin.messenger.infrastructure.service.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class SendOrReplyToMessageDto {
    private UUID chatId;

    private UUID userId;

    private Message messageToReply;

    @Min(1)
    private List<MessageDataDto> dataDtos;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;
}
