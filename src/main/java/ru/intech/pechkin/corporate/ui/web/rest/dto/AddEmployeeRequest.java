package ru.intech.pechkin.corporate.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddEmployeeRequest {
    private String fio;
    private String phoneNumber;
    private String email;
    private UUID department;
    private String position;
    private Boolean superuser;
}
