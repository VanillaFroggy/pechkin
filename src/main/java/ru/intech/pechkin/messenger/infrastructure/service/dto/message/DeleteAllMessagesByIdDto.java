package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeleteAllMessagesByIdDto {
    @NotNull
    private final UUID chatId;

    @Size(min = 2)
    private final List<UUID> messageIds;
}
