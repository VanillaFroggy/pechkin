package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SetMessageCheckedDto {
    private UUID chatId;
    private UUID userId;
    private UUID publisherId;
    private UUID messageId;
}
