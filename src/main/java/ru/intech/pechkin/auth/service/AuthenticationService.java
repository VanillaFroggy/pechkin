package ru.intech.pechkin.auth.service;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.auth.service.dto.RegisterDto;

@Validated
public interface AuthenticationService {
    AuthenticationDto register(@Valid RegisterDto dto);

    AuthenticationDto authenticate(@Valid AuthenticateDto dto);
}
