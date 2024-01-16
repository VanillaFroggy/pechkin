package ru.intech.pechkin.messenger.infrastructure.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateMessageListDto {
    private UUID chatId;

    private UUID userId;

    @Size()
    private int pageNumber;

    @Size(min = 1, max = 50)
    private int pageSize;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;
}
