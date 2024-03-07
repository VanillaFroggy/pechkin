package ru.intech.pechkin.messenger.infrastructure.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UpdateUserIconDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UserDto;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final EmployeeService employeeService;
    private final MessengerServiceMapper mapper;

    @Override
    public UserDto getUserById(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(NullPointerException::new);
        return mapper.userAndEployeeDtoToUserDto(
                user,
                employeeService.getEmployeeById(user.getEmployeeId())
        );
    }

    @Override
    public UserDto getUserByUsername(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(NullPointerException::new);
        return mapper.userAndEployeeDtoToUserDto(
                user,
                employeeService.getEmployeeById(user.getEmployeeId())
        );
    }

    @Override
    public List<UserDto> getUserListByUsernameLike(String username) {
        List<User> users = repository.findAllByUsernameLikeIgnoreCase(username);
        if (users.isEmpty()) {
            throw new NullPointerException();
        }
        return users.stream()
                .map(user -> mapper.userAndEployeeDtoToUserDto(
                        user,
                        employeeService.getEmployeeById(user.getEmployeeId())
                ))
                .toList();
    }

    @Override
    public void updateUserIcon(UpdateUserIconDto dto) {
        User user = repository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        user.setIcon(dto.getIcon());
        repository.save(user);
    }

    @Override
    public void blockUser(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(NullPointerException::new);
        user.setBlocked(true);
        repository.save(user);
    }

    @Override
    public void unblockUser(UUID id) {
        User user = repository.findById(id)
                .orElseThrow(NullPointerException::new);
        user.setBlocked(false);
        repository.save(user);
    }
}
