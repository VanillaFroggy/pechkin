package ru.intech.pechkin.messenger.ui.web.rest.dto.chat;

import lombok.Data;

import java.util.UUID;

@Data
public class GetP2PChatByUsersRequest {
    private final UUID userId;
    private final UUID searchedUserId;
}
