package ru.intech.pechkin.auth.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.intech.pechkin.auth.service.AuthenticationService;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.auth.ui.web.rest.dto.AuthenticateRequest;
import ru.intech.pechkin.auth.ui.web.rest.dto.AuthenticationResponse;
import ru.intech.pechkin.auth.ui.web.rest.dto.RegisterRequest;
import ru.intech.pechkin.auth.ui.web.rest.mapper.AuthenticationRestMapper;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final AuthenticationRestMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationDto dto = service.register(mapper.registerRequestToDto(request));
        return ResponseEntity.ok()
                .header("Authorization", dto.getToken())
                .header("Access-Control-Expose-Headers", "Authorization")
                .body(mapper.authenticationDtoToResponse(dto));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticateRequest request) {
        AuthenticationDto dto = service.authenticate(mapper.authenticateRequestToDto(request));
        return ResponseEntity.ok()
                .header("Authorization", dto.getToken())
                .header("Access-Control-Expose-Headers", "Authorization")
                .body(mapper.authenticationDtoToResponse(dto));
    }
}
