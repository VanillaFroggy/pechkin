package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageData;

import java.util.List;
import java.util.UUID;

@Data
public class EditMessageDto {
    @NotNull
    private UUID chatId;

    @NotNull
    private UUID messageId;

    @Size(min = 1, max = 10)
    private List<MessageData> datas;
}
