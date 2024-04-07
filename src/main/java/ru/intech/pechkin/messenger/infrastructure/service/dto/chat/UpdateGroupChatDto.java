package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
public class UpdateGroupChatDto {
    @NotNull
    private UUID chatId;

    @NotNull
    private String title;

    @NotNull
    private String icon;

    @Size(max = 100_000)
    private Map<UUID, Role> users;
}
