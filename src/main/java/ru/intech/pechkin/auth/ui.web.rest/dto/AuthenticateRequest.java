package ru.intech.pechkin.auth.ui.web.rest.dto;

import lombok.Data;

@Data
public class AuthenticateRequest {
    private String username;
    private String password;
}
