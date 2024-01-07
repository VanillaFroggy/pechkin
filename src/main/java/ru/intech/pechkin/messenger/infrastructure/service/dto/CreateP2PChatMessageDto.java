package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatMessageDto {
    private UUID publisher;

    private List<MessageDataDto> dataDtos;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;
}
