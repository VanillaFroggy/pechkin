package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.Min;
import lombok.Data;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageData;

import java.util.List;
import java.util.UUID;

@Data
public class EditMessageDto {
    private UUID chatId;

    private UUID messageId;

    @Min(1)
    private List<MessageData> datas;
}
