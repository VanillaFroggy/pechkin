package ru.intech.pechkin.auth.ui.web.rest.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthenticationResponse {
    private UUID id;
    private String username;
    private String icon;
    private String fio;
    private String phoneNumber;
    private String email;
    private String department;
    private String position;
}