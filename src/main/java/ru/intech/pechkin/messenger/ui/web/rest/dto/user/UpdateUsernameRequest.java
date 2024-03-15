package ru.intech.pechkin.messenger.ui.web.rest.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUsernameRequest {
    private final UUID userId;
    private final String username;
}
