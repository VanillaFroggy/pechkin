package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class CreateGroupChatDto {
    private final UUID departmentId;

    @NotNull
    private final String title;

    @NotNull
    private final String icon;

    @Size(max = 100_000)
    private final Map<UUID, Role> users;

    @NotNull
    private final Boolean corporate;
}
