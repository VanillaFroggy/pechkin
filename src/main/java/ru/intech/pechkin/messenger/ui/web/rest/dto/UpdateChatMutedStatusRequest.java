package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateChatMutedStatusRequest {
    private UUID userId;
    private UUID chatId;
    private Boolean muted;
}
