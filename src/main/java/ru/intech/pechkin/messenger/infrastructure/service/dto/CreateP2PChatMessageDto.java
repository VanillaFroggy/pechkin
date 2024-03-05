package ru.intech.pechkin.messenger.infrastructure.service.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatMessageDto {
    private UUID publisher;

    @Min(1)
    private List<MessageDataDto> dataDtos;
}
