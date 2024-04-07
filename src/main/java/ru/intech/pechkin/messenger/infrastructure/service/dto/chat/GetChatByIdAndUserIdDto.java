package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class GetChatByIdAndUserIdDto {
    @NotNull
    private final UUID chatId;

    @NotNull
    private final UUID userId;
}
