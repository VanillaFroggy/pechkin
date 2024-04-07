package ru.intech.pechkin.messenger.ui.web.rest.dto.message;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class SetMessageListCheckedRequest {
    private final UUID chatId;
    private final UUID userId;
    private final Map<UUID, UUID> messagesWithPublishers;
}
