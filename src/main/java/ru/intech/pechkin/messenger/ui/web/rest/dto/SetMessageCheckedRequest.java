package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SetMessageCheckedRequest {
    private UUID chatId;
    private UUID userId;
    private UUID messageId;
}
