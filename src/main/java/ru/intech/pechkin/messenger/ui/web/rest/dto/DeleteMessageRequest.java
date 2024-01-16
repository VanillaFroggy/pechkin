package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteMessageRequest {
    private UUID chatId;
    private UUID messageId;
}
