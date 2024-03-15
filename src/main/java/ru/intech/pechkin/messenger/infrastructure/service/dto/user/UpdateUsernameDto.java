package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUsernameDto {
    private final UUID userId;

    @Pattern(regexp = "^\\w{4,32}$")
    private final String username;
}
