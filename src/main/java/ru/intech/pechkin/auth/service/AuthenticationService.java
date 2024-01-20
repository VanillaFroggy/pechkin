package ru.intech.pechkin.auth.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.auth.service.dto.RegisterDto;

public interface AuthenticationService {
    void register(@Valid RegisterDto dto);

    AuthenticationDto authenticate(@Valid AuthenticateDto dto);
}
