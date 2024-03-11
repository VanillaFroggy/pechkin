package ru.intech.pechkin.messenger.ui.web.rest.dto.chat;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.service.dto.chat.CreateP2PChatMessageDto;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatRequest {
    List<UUID> users;
    CreateP2PChatMessageDto messageDto;
}
