package ru.intech.pechkin.auth.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.AuthenticationResponse;
import ru.intech.pechkin.auth.service.dto.RegisterDto;

public interface AuthenticationService {
    AuthenticationResponse register(@Valid RegisterDto dto);

    AuthenticationResponse authenticate(@Valid AuthenticateDto dto);
}
