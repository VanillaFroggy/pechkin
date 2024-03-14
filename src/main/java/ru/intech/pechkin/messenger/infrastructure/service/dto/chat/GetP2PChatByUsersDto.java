package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class GetP2PChatByUsersDto {
    private final UUID userId;
    private final UUID searchedUserId;
}
