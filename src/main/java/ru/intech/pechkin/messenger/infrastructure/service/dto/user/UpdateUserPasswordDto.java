package ru.intech.pechkin.messenger.infrastructure.service.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserPasswordDto {

    private final UUID userId;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,50}$")
    private final String password;
}
