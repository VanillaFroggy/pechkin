package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.Min;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDataDto;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatMessageDto {
    private UUID publisher;

    @Min(1)
    private List<MessageDataDto> dataDtos;
}
