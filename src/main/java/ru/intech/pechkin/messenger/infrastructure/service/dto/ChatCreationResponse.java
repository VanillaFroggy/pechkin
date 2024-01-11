package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ChatCreationResponse {
    private UUID chatId;
}
