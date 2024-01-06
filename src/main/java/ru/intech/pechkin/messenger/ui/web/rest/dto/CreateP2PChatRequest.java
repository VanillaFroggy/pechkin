package ru.intech.pechkin.messenger.ui.web.rest.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateP2PChatRequest {
    List<UUID> users;
}
