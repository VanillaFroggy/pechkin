package ru.intech.pechkin.messenger.infrastructure.service;

import ru.intech.pechkin.messenger.infrastructure.service.dto.UserDto;

import java.util.UUID;

public interface UserService {
    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);
}
