package ru.intech.pechkin.auth.ui.web.rest.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String icon;
    private String fio;
    private String phoneNumber;
    private String email;
    private String department;
    private String position;
}
