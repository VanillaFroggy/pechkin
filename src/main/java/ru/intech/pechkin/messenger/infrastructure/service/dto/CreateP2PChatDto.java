package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatDto {
    List<UUID> users;
    CreateP2PChatMessageDto messageDto;
}
