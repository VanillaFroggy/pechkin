package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateEmployeeRequest {
    private UUID id;
    private String fio;
    private String phoneNumber;
    private String email;
    private UUID department;
    private String position;
    private Boolean superuser;
}
