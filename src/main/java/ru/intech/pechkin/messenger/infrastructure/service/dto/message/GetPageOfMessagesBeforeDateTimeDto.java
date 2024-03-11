package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class GetPageOfMessagesBeforeDateTimeDto {
    private final UUID chatId;

    private final UUID userId;

    @Size()
    private final int pageNumber;

    @Size(min = 1, max = 50)
    private final int pageSize;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private final ZonedDateTime dateTime;
}
