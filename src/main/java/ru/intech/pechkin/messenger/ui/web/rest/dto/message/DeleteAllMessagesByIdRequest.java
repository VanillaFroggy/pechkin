package ru.intech.pechkin.messenger.ui.web.rest.dto.message;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DeleteAllMessagesByIdRequest {
    private final UUID chatId;
    private final List<UUID> messageIds;
}
