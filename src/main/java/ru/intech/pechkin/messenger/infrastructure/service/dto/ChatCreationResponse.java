package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class ChatCreationResponse {
    private final UUID chatId;
}
