package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateDepartmentRequest {
    private final UUID id;
    private final String title;
    private final UUID parent;
}
