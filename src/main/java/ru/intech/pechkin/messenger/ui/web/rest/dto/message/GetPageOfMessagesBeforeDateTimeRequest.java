package ru.intech.pechkin.messenger.ui.web.rest.dto.message;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class GetPageOfMessagesBeforeDateTimeRequest {
    private UUID chatId;
    private UUID userId;
    private int pageNumber;
    private int pageSize;
    private ZonedDateTime dateTime;
}
