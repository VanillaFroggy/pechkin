package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class FindMessageByValueDto {
    private UUID chatId;
    private String value;
}
