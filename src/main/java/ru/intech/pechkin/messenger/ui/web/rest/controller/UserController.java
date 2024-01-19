package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UserDto;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(
                userService.getUserById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("username") String username) {
        return new ResponseEntity<>(
                userService.getUserByUsername(username),
                HttpStatus.OK
        );
    }
}
