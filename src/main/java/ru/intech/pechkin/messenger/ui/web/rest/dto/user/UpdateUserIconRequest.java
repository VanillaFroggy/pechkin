package ru.intech.pechkin.messenger.ui.web.rest.dto.user;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserIconRequest {
    private UUID userId;
    private String icon;
}
