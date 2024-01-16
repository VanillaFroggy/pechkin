package ru.intech.pechkin.messenger.infrastructure.service.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfMessagesDto {
    private UUID chatId;

    @Size()
    private int pageNumber;

    @Size(min = 1, max = 50)
    private int pageSize;
}
