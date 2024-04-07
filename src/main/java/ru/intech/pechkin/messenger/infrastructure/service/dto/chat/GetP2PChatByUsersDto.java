package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GetP2PChatByUsersDto {
    @NotNull
    private final UUID userId;

    @NotNull
    private final UUID searchedUserId;
}
