package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeleteAllMessagesByIdDto {
    private final UUID chatId;
    private final List<UUID> messageIds;
}
