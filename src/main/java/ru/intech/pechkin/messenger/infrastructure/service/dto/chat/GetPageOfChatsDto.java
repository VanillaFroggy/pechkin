package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class GetPageOfChatsDto {
    private final UUID userId;

    @Size()
    private final int pageNumber;

    @Size(min = 1, max = 25)
    private final int pageSize;
}
