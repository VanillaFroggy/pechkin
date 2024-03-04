package ru.intech.pechkin.auth.ui.web.rest.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RegisterRequest {
    private UUID employeeId;
    private String username;
    private String password;
    private String icon;
}
