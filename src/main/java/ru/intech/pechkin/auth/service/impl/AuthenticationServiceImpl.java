package ru.intech.pechkin.auth.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.auth.config.JwtService;
import ru.intech.pechkin.auth.service.AuthenticationService;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.auth.service.dto.RegisterDto;
import ru.intech.pechkin.auth.service.exception.IllegalRegisterParameterException;
import ru.intech.pechkin.auth.service.exception.NoSuchUsernameAndPasswordException;
import ru.intech.pechkin.auth.service.mapper.AuthenticationServiceMapper;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationServiceMapper mapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ChatService chatService;

    @Override
    public void register(@Valid RegisterDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalRegisterParameterException("Пользователь с таким именем уже существует");
        } else if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalRegisterParameterException("Этот email уже зарегестрирован");
        } else if (userRepository.findByPhoneNumber(dto.getPhoneNumber()).isPresent()) {
            throw new IllegalRegisterParameterException("Этот номер телефона уже зарегестрирован");
        }

        User user = User.builder()
                .id(UUID.randomUUID())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .icon(dto.getIcon())
                .fio(dto.getFio())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .department(dto.getDepartment())
                .position(dto.getPosition())
                .blocked(false)
                .build();
        chatService.createFavoritesChat(user.getId());
        userRepository.save(user);
    }

    @Override
    public AuthenticationDto authenticate(@Valid AuthenticateDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(NoSuchUsernameAndPasswordException::new);
        return mapper.entityToDto(jwtService.generateToken(user), user);
    }
}
