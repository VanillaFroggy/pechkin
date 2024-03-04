package ru.intech.pechkin.auth.service.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class RegisterDto {
    private UUID employeeId;

    @Pattern(regexp = "^\\w{4,32}$")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,50}$")
    private String password;

    private String icon;
}
