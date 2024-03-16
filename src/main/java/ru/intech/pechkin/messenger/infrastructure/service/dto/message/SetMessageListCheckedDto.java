package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SetMessageListCheckedDto {
    private final UUID chatId;
    private final UUID userId;
    private final UUID publisherId;
    private final List<UUID> messageIds;
}
