package ru.intech.pechkin.messenger.infrastructure.service.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatDto {
    @Size(min = 2, max = 2)
    List<UUID> users;

    @NotNull
    CreateP2PChatMessageDto messageDto;
}
