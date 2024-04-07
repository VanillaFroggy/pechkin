package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SendMessageDto {
    @NotNull
    private UUID chatId;

    @NotNull
    private UUID userId;

    @Size(min = 1, max = 10)
    private List<MessageDataDto> dataDtos;
}
