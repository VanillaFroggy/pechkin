package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserIconDto {
    @NotNull
    private UUID userId;

    @NotNull
    private String icon;
}
