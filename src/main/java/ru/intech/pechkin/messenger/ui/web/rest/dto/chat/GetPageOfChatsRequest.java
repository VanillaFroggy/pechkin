package ru.intech.pechkin.messenger.ui.web.rest.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfChatsRequest {
    private UUID chatId;
    private UUID userId;
    private int pageNumber;
    private int pageSize;
}
