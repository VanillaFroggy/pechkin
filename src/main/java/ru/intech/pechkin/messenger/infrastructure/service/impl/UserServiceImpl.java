package ru.intech.pechkin.messenger.infrastructure.service.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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
    public Page<UserDto> getPageOfUsersByUsernameLike(GetPageOfUsersByFieldLikeDto dto) {
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
                        .toList()
        );
    }

    @Override
    public Page<UserDto> getPageOfUsersByDepartment(GetPageOfUsersByDepartmentDto dto) {
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
    public Page<UserDto> getPageOfUsersByDepartmentLike(GetPageOfUsersByFieldLikeDto dto) {
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
    public Page<UserDto> getPageOfUsersByFioLike(GetPageOfUsersByFieldLikeDto dto) {
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
    public Page<UserDto> getPageOfUsersByPositionLike(GetPageOfUsersByFieldLikeDto dto) {
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

    @NotNull
    private PageImpl<UserDto> getUserDtosPage(Page<EmployeeDto> employeeDtos) {
        Page<User> users = repository.findAllByEmployeeIdIn(
                employeeDtos.stream()
                        .map(EmployeeDto::getId)
                        .toList(),
                employeeDtos.getPageable()
        );
        return new PageImpl<>(
                users.stream()
                        .map(user -> mapper.userAndEployeeDtoToUserDto(
                                user,
                                employeeDtos.stream()
                                        .filter(employeeDto -> employeeDto.getId().equals(user.getEmployeeId()))
                                        .findFirst()
                                        .orElseThrow(NullPointerException::new)
                        ))
                        .sorted(
                                Comparator.comparing((UserDto userDto) -> userDto.getDepartment().getTitle())
                                        .thenComparing(UserDto::getFio)
                        )
                        .toList()
        );
    }

    private static void checkPageEmptiness(Page<?> page) {
        if (page.isEmpty()) {
            throw new NullPointerException("List of users is empty");
        }
    }

    @Override
    public void updateUsername(UpdateUsernameDto dto) {
        User user = repository.findById(dto.getUserId())
                .orElseThrow(NullPointerException::new);
        user.setUsername(dto.getUsername());
        repository.save(user);
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
