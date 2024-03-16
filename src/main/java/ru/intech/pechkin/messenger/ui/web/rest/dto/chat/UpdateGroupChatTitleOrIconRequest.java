package ru.intech.pechkin.messenger.ui.web.rest.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateGroupChatTitleOrIconRequest {
    private final UUID chatId;
    private final String value;
}
