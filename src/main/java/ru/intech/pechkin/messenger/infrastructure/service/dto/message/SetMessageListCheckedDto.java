package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class SetMessageListCheckedDto {
    @NotNull
    private final UUID chatId;

    @NotNull
    private final UUID userId;

    @Size(min = 2)
    private final Map<UUID, UUID> messagesWithPublishers;
}
