package ru.intech.pechkin.messenger.infrastructure.service;

import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UpdateUserIconDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);

    List<UserDto> getUserListByUsernameLike(String username);

    void updateUserIcon(UpdateUserIconDto dto);

    void blockUser(UUID id);

    void unblockUser(UUID id);
}
