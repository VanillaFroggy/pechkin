package ru.intech.pechkin.messenger.infrastructure.service.dto.message;

import lombok.Data;

import java.util.UUID;

@Data
public class MessagePublisherDto {
    private final UUID id;
    private final String username;
    private final String icon;
}
