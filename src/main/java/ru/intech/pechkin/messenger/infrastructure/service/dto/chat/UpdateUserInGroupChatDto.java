package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.UUID;

@Data
public class UpdateUserInGroupChatDto {
    @NotNull
    private final UUID chatId;

    @NotNull
    private final UUID userId;

    @NotNull
    private final Role userRole;
}
