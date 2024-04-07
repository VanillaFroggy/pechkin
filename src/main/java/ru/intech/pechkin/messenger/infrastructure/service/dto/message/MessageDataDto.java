package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageType;

@Data
@RequiredArgsConstructor
public class MessageDataDto {
    @NotNull
    private final MessageType messageType;

    @NotNull
    private final String value;
}
