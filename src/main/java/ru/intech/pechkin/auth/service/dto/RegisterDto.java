package ru.intech.pechkin.auth.service.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDto {
    @Pattern(regexp = "^\\w{4,32}$")
    private String username;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{12,50}$")
    private String password;

    private String fio;

    @Pattern(regexp = "^(\\+7|8)[(]?\\d{3}[)]?[-\\s\\\\.]?\\d{3}[-\\s.]?\\d{4}$")
    private String phoneNumber;

    @Pattern(regexp = "^[\\w.%+-]+@[a-z\\d.-]+\\\\.[a-z]{2,6}$")
    private String email;

    @Pattern(regexp = "^\\w{4,32}$")
    private String department;

    @Pattern(regexp = "^\\w{4,32}$")
    private String position;
}
