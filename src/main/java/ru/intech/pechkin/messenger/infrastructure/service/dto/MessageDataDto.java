package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageType;

@Data
@RequiredArgsConstructor
public class MessageDataDto {
    private final MessageType messageType;
    private final String value;
}
