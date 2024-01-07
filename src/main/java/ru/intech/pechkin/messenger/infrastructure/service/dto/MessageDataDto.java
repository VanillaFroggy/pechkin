package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.MessageType;

@Data
public class MessageDataDto {
    private MessageType messageType;
    private String value;
}
