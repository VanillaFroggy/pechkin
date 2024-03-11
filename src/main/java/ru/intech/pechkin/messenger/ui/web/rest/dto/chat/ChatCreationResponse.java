package ru.intech.pechkin.messenger.ui.web.rest.dto.chat;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
public class ChatCreationResponse {
    private final UUID chatId;
}
