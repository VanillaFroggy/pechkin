package ru.intech.pechkin.auth.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.auth.config.JwtService;
import ru.intech.pechkin.auth.service.AuthenticationService;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.auth.service.dto.RegisterDto;
import ru.intech.pechkin.auth.service.exception.IllegalRegisterParameterException;
import ru.intech.pechkin.auth.service.exception.NoSuchUsernameAndPasswordException;
import ru.intech.pechkin.auth.service.mapper.AuthenticationServiceMapper;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.mapper.CorporateServiceMapper;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.service.ChatService;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final EmployeeService employeeService;
    private final CorporateServiceMapper corporateServiceMapper;
    private final AuthenticationServiceMapper authenticationServiceMapper;

    @Override
    public AuthenticationDto register(@Valid RegisterDto dto) {
        EmployeeDto employeeDto = employeeService.getEmployeeById(dto.getEmployeeId());
        if (userRepository.findByEmployeeId(dto.getEmployeeId()).isPresent()) {
            throw new IllegalRegisterParameterException("This employee is already registered");
        } else if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalRegisterParameterException("User with this username already exists");
        }
        User user = User.builder()
                .id(UUID.randomUUID())
                .employeeId(dto.getEmployeeId())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .icon(dto.getIcon())
                .blocked(false)
                .build();
        chatService.createFavoritesChat(user.getId());
        userRepository.save(user);
        if (employeeDto.getDepartment() != null) {
            employeeService.updateEmployee(
                    corporateServiceMapper.employeeDtoToUpdateEmployeeDto(employeeDto)
            );
        }
        return authenticationServiceMapper.userAndEmployeeDtoToAuthenticationDto(
                jwtService.generateToken(user),
                user,
                employeeDto
        );
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
        return authenticationServiceMapper.userAndEmployeeDtoToAuthenticationDto(
                jwtService.generateToken(user),
                user,
                employeeService.getEmployeeById(user.getEmployeeId())
        );
    }
}
