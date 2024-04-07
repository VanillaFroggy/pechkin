package ru.intech.pechkin.messenger.infrastructure.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.*;

import java.util.UUID;

@Validated
public interface UserService {
    UserDto getUserById(@NotNull UUID id);

    UserDto getUserByUsername(@NotNull String username);

    Page<UserDto> getPageOfUsersByUsernameLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    Page<UserDto> getPageOfUsersByDepartment(@Valid GetPageOfUsersByDepartmentDto dto);

    Page<UserDto> getPageOfUsersByDepartmentLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    Page<UserDto> getPageOfUsersByFioLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    Page<UserDto> getPageOfUsersByPositionLike(@Valid GetPageOfUsersByFieldLikeDto dto);

    void updateUsername(@Valid UpdateUsernameDto dto);

    void updateUserIcon(@Valid UpdateUserIconDto dto);

    void blockUser(@NotNull UUID id);

    void unblockUser(@NotNull UUID id);
}
