package ru.intech.pechkin.messenger.infrastructure.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistance.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UpdateUserIconDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UserDto;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final MessengerServiceMapper mapper;

    @Override
    public UserDto getUserById(UUID id) {
        return mapper.userToUserDto(
                repository.findById(id)
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return mapper.userToUserDto(
                repository.findByUsername(username)
                        .orElseThrow(NullPointerException::new)
        );
    }

    @Override
    public void updateUserIcon(UpdateUserIconDto dto) {
        User user = repository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        user.setIcon(dto.getIcon());
        repository.save(user);
    }
}
