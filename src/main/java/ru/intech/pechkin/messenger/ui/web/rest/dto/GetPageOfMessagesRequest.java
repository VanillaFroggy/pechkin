package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfMessagesRequest {
    private UUID chatId;
    private UUID userId;
    private int pageNumber;
    private int pageSize;
}
