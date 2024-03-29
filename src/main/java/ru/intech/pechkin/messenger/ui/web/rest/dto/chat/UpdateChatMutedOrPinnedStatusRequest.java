package ru.intech.pechkin.messenger.ui.web.rest.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateChatMutedOrPinnedStatusRequest {
    private UUID userId;
    private UUID chatId;
    private Boolean status;
}
