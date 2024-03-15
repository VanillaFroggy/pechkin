package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.*;

import java.util.UUID;

public interface UserService {
    UserDto getUserById(UUID id);

    UserDto getUserByUsername(String username);

    Page<UserDto> getPageOfUsersByUsernameLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    Page<UserDto> getPageOfUsersByDepartment(@Valid GetPageOfUsersByDepartmentDto dto);

    Page<UserDto> getPageOfUsersByDepartmentLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    Page<UserDto> getPageOfUsersByFioLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    Page<UserDto> getPageOfUsersByPositionLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    void updateUsername(@Valid UpdateUsernameDto dto);

    void updateUserIcon(UpdateUserIconDto dto);

    void blockUser(UUID id);

    void unblockUser(UUID id);
}
