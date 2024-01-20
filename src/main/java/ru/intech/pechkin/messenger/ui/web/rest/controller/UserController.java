package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UserDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.UpdateUserIconRequest;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.UserRestMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRestMapper mapper;

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

    @PutMapping("/updateUserIcon")
    public ResponseEntity<Void> updateUserIcon(@RequestBody UpdateUserIconRequest request) {
        userService.updateUserIcon(mapper.updateUserIconRequestToDto(request));
        return ResponseEntity.noContent().build();
    }
}