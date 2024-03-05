package ru.intech.pechkin.messenger.infrastructure.service.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class SendMessageDto {
    private UUID chatId;

    private UUID userId;

    @Min(1)
    private List<MessageDataDto> dataDtos;
}
