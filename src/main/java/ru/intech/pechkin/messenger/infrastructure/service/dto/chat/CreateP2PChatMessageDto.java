package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDataDto;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatMessageDto {
    @NotNull
    private UUID publisher;

    @Size(min = 1, max = 10)
    private List<MessageDataDto> dataDtos;
}
