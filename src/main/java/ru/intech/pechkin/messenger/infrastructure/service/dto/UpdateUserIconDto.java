package ru.intech.pechkin.messenger.infrastructure.service.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserIconDto {
    private UUID userId;
    private String icon;
}
