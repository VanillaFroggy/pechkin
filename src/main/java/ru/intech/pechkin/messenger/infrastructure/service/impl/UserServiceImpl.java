package ru.intech.pechkin.messenger.infrastructure.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.GetPageOfEmployeesByDepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.GetPageOfEmployeesByFieldLikeDto;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.*;
import ru.intech.pechkin.messenger.infrastructure.service.mapper.MessengerServiceMapper;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final EmployeeService employeeService;
    private final MessengerServiceMapper mapper;

    @Override
    public UserDto getUserById(@NotNull UUID id) {
        User user = repository.findById(id)
                .orElseThrow(NullPointerException::new);
        return mapper.userAndEployeeDtoToUserDto(
                user,
                employeeService.getEmployeeById(user.getEmployeeId())
        );
    }

    @Override
    public UserDto getUserByUsername(@NotNull String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(NullPointerException::new);
        return mapper.userAndEployeeDtoToUserDto(
                user,
                employeeService.getEmployeeById(user.getEmployeeId())
        );
    }

    @Override
    public Page<UserDto> getPageOfUsersByUsernameLike(@Valid GetPageOfUsersByFieldLikeDto dto) {
        Page<User> users = repository.findAllByUsernameLikeIgnoreCase(
                dto.getValue(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        checkPageEmptiness(users);
        return new PageImpl<>(
                users.stream()
                        .map(user -> mapper.userAndEployeeDtoToUserDto(
                                user,
                                employeeService.getEmployeeById(user.getEmployeeId())
                        ))
                        .toList(),
                users.getPageable(),
                users.getTotalElements()
        );
    }

    @Override
    public Page<UserDto> getPageOfUsersByDepartment(@Valid GetPageOfUsersByDepartmentDto dto) {
        Page<EmployeeDto> employeeDtos = employeeService.getPageOfEmployeesByDepartment(
                new GetPageOfEmployeesByDepartmentDto(
                        dto.getDepartmentId(),
                        dto.getPageNumber(),
                        dto.getPageSize()
                )
        );
        checkPageEmptiness(employeeDtos);
        return getUserDtosPage(employeeDtos);
    }

    @Override
    public Page<UserDto> getPageOfUsersByDepartmentLike(@Valid GetPageOfUsersByFieldLikeDto dto) {
        Page<EmployeeDto> employeeDtos = employeeService.getPageOfEmployeesByDepartmentLike(
                new GetPageOfEmployeesByFieldLikeDto(
                        dto.getValue(),
                        dto.getPageNumber(),
                        dto.getPageSize()
                )
        );
        checkPageEmptiness(employeeDtos);
        return getUserDtosPage(employeeDtos);
    }

    @Override
    public Page<UserDto> getPageOfUsersByFioLike(@Valid GetPageOfUsersByFieldLikeDto dto) {
        Page<EmployeeDto> employeeDtos = employeeService.getPageOfEmployeesByFioLike(
                new GetPageOfEmployeesByFieldLikeDto(
                        dto.getValue(),
                        dto.getPageNumber(),
                        dto.getPageSize()
                )
        );
        checkPageEmptiness(employeeDtos);
        return getUserDtosPage(employeeDtos);
    }

    @Override
    public Page<UserDto> getPageOfUsersByPositionLike(@Valid GetPageOfUsersByFieldLikeDto dto) {
        Page<EmployeeDto> employeeDtos = employeeService.getPageOfEmployeesByPositionLike(
                new GetPageOfEmployeesByFieldLikeDto(
                        dto.getValue(),
                        dto.getPageNumber(),
                        dto.getPageSize()
                )
        );
        checkPageEmptiness(employeeDtos);
        return getUserDtosPage(employeeDtos);
    }

    @NonNull
    private PageImpl<UserDto> getUserDtosPage(Page<EmployeeDto> employeeDtos) {
        Map<UUID, EmployeeDto> employeeDtoMap = employeeDtos.stream()
                .collect(Collectors.toMap(EmployeeDto::getId, employeeDto -> employeeDto));
        return new PageImpl<>(
                repository.findAllByEmployeeIdIn(employeeDtoMap.keySet(), employeeDtos.getPageable())
                        .stream()
                        .map(user -> mapper.userAndEployeeDtoToUserDto(
                                user,
                                employeeDtoMap.get(user.getEmployeeId())
                        ))
                        .sorted(
                                Comparator.comparing((UserDto userDto) -> userDto.getDepartment().getTitle())
                                        .thenComparing(UserDto::getFio)
                        )
                        .toList(),
                employeeDtos.getPageable(),
                employeeDtos.getTotalElements()
        );
    }

    private static void checkPageEmptiness(Page<?> page) {
        if (page.isEmpty()) {
            throw new NullPointerException("List of users is empty");
        }
    }

    @Override
    public void updateUsername(@Valid UpdateUsernameDto dto) {
        User user = repository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        Optional<User> optionalUser = repository.findByUsername(dto.getUsername());
        if (optionalUser.isPresent() && !optionalUser.get().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User with this username is already exists");
        }
        user.setUsername(dto.getUsername());
        repository.save(user);
    }

    @Override
    public void updateUserIcon(@Valid UpdateUserIconDto dto) {
        User user = repository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        user.setIcon(dto.getIcon());
        repository.save(user);
    }

    @Override
    public void blockUser(@NotNull UUID id) {
        User user = repository.findById(id)
                .orElseThrow(NullPointerException::new);
        user.setBlocked(true);
        repository.save(user);
    }

    @Override
    public void unblockUser(@NotNull UUID id) {
        User user = repository.findById(id)
                .orElseThrow(NullPointerException::new);
        user.setBlocked(false);
        repository.save(user);
    }
}
