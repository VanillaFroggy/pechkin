package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteMessageDto {
    private UUID chatId;
    private UUID messageId;
}
