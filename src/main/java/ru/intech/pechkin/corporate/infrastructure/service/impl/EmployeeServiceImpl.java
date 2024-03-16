package ru.intech.pechkin.corporate.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.auth.service.exception.IllegalRegisterParameterException;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.DepartmentRepository;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.EmployeeRepository;
import ru.intech.pechkin.corporate.infrastructure.service.DepartmentService;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;
import ru.intech.pechkin.corporate.infrastructure.service.mapper.CorporateServiceMapper;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.MessageType;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.Role;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.UserRoleMutedPinnedChat;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.ChatRepository;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRoleMutedPinnedChatRepository;
import ru.intech.pechkin.messenger.infrastructure.service.MessageService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.MessageDataDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.message.SendMessageDto;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final UserRoleMutedPinnedChatRepository userRoleMutedPinnedChatRepository;
    private final ChatRepository chatRepository;
    private final MessageService messageService;
    private final DepartmentService departmentService;
    private final CorporateServiceMapper mapper;

    @Value("${registration-link}")
    private String registrationLink;

    @Override
    public Page<EmployeeDto> getPageOfEmployees(GetPageOfEmployeesDto dto) {
        Page<Employee> employees = employeeRepository.findAll(PageRequest.of(dto.getPageNumber(), dto.getPageSize()));
        checkPageEmptiness(employees);
        return getEmployeeDtosPage(employees);
    }

    @Override
    public EmployeeDto getEmployeeById(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(NullPointerException::new);
        DepartmentDto departmentDto = null;
        if (employee.getDepartment() != null) {
            departmentDto = departmentService.getDepartmentById(employee.getDepartment());
        }
        return mapper.employeeToDto(employee, departmentDto);
    }

    @Override
    public Page<EmployeeDto> getPageOfEmployeesByDepartment(GetPageOfEmployeesByDepartmentDto dto) {
        DepartmentDto departmentDto = departmentService.getDepartmentById(dto.getDepartmentId());
        Page<Employee> employees = employeeRepository.findAllByDepartment(
                departmentDto.getId(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        checkPageEmptiness(employees);
        return new PageImpl<>(
                employees.stream()
                        .map(employee -> mapper.employeeToDto(
                                employee,
                                departmentDto
                        ))
                        .sorted(
                                Comparator.comparing((EmployeeDto employeeDto) ->
                                                employeeDto.getDepartment().getTitle())
                                        .thenComparing(EmployeeDto::getFio)
                        )
                        .toList()
        );
    }

    @Override
    public Page<EmployeeDto> getPageOfEmployeesByDepartmentLike(GetPageOfEmployeesByFieldLikeDto dto) {
        List<DepartmentDto> departmentDtos = departmentService.getDepartmentsByTitleLike(dto.getValue());
        Page<Employee> employees = employeeRepository.findAllByDepartmentIn(
                departmentDtos.stream()
                        .map(DepartmentDto::getId)
                        .toList(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        checkPageEmptiness(employees);
        return new PageImpl<>(
                employees.stream()
                        .map(employee -> mapper.employeeToDto(
                                employee,
                                departmentDtos.stream()
                                        .filter(departmentDto ->
                                                departmentDto.getId().equals(employee.getDepartment()))
                                        .findFirst()
                                        .orElse(null)
                        ))
                        .toList()
        );
    }

    @Override
    public Page<EmployeeDto> getPageOfEmployeesByFioLike(GetPageOfEmployeesByFieldLikeDto dto) {
        Page<Employee> employees = employeeRepository.findByFioLikeIgnoreCase(
                dto.getValue(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        checkPageEmptiness(employees);
        return getEmployeeDtosPage(employees);
    }

    @Override
    public Page<EmployeeDto> getPageOfEmployeesByPositionLike(GetPageOfEmployeesByFieldLikeDto dto) {
        Page<Employee> employees = employeeRepository.findByPositionLikeIgnoreCase(
                dto.getValue(),
                PageRequest.of(dto.getPageNumber(), dto.getPageSize())
        );
        checkPageEmptiness(employees);
        return getEmployeeDtosPage(employees);
    }

    private void checkPageEmptiness(Page<?> page) {
        if (page.isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
    }

    private @NonNull Page<EmployeeDto> getEmployeeDtosPage(Page<Employee> employees) {
        List<Department> departments = departmentRepository.findAllById(
                employees.stream()
                        .map(Employee::getDepartment)
                        .toList()
        );
        return new PageImpl<>(
                employees
                        .stream()
                        .map(employee -> mapper.employeeToDto(
                                employee,
                                mapper.departmentToDto(
                                        departments.stream()
                                                .filter(department ->
                                                        department.getId().equals(employee.getDepartment()))
                                                .findFirst()
                                                .orElse(null)
                                )
                        ))
                        .sorted(
                                Comparator.comparing((EmployeeDto employeeDto) ->
                                                employeeDto.getDepartment().getTitle())
                                        .thenComparing(EmployeeDto::getFio)
                        )
                        .toList()
        );
    }

    @Override
    public EmployeeRegistrationResponse addEmployee(@Valid AddEmployeeDto dto) {
        checkEmployeeContacts(null, dto.getEmail(), dto.getPhoneNumber());
        Employee employee = mapper.addEmployeeDtoToEntity(UUID.randomUUID(), dto, false);
        employeeRepository.save(employee);
        return new EmployeeRegistrationResponse(registrationLink + "?key=" + employee.getId());
    }

    @Override
    public void updateEmployee(@Valid UpdateEmployeeDto dto) {
        checkEmployeeContacts(dto.getId(), dto.getEmail(), dto.getPhoneNumber());
        Employee employee = employeeRepository.findById(dto.getId())
                .orElseThrow(NullPointerException::new);
        Optional<User> optionalUser = userRepository.findByEmployeeId(dto.getId());
        if (dto.getDepartment() != null && !dto.getDepartment().equals(employee.getDepartment())
                && employee.getDepartment() != null
                && optionalUser.isPresent()) {
            removeEmployeeFromCorporateChat(dto, optionalUser);
        }
        if (dto.getDepartment() != null && optionalUser.isPresent()) {
            addEmployeeToCorporateChat(dto, optionalUser);
        }

        if (dto.getFired()) {
            fireEmployee(dto.getId());
        } else if (employee.getFired()) {
            updateUserBlockedStatus(employee.getId(), false);
        }
        employeeRepository.save(mapper.updateEmployeeDtoToEntity(dto));
    }

    private void checkEmployeeContacts(UUID employeeId, String email, String phoneNumber) {
        Optional<Employee> employeeByEmail = employeeRepository.findByEmail(email);
        Optional<Employee> employeeByPhoneNumber = employeeRepository.findByPhoneNumber(phoneNumber);

        if (employeeByEmail.isPresent() && !employeeByEmail.get().getId().equals(employeeId)) {
            throw new IllegalRegisterParameterException("This email has already been registered");
        } else if (employeeByPhoneNumber.isPresent() && !employeeByPhoneNumber.get().getId().equals(employeeId)) {
            throw new IllegalRegisterParameterException("This phone number is already registered");
        }
    }

    private void removeEmployeeFromCorporateChat(UpdateEmployeeDto dto, Optional<User> optionalUser) {
        UUID chatId = chatRepository.findByDepartmentId(dto.getDepartment())
                .orElseThrow(NullPointerException::new)
                .getId();
        sendMessageToCorporateChat(chatId, "@" + optionalUser.get().getUsername() + " left the group");
        userRoleMutedPinnedChatRepository.deleteByUserIdAndChatId(
                optionalUser
                        .orElseThrow(NullPointerException::new)
                        .getId(),
                chatId
        );
    }

    private void addEmployeeToCorporateChat(UpdateEmployeeDto dto, Optional<User> optionalUser) {
        UUID chatId = chatRepository.findByDepartmentId(dto.getDepartment())
                .orElseThrow(NullPointerException::new)
                .getId();
        UserRoleMutedPinnedChat userRoleMutedPinnedChat =
                userRoleMutedPinnedChatRepository.findByUserIdAndChatId(
                        optionalUser.orElseThrow(NullPointerException::new)
                                .getId(),
                        chatId
                );
        if (userRoleMutedPinnedChat == null) {
            userRoleMutedPinnedChat = UserRoleMutedPinnedChat.create(
                    optionalUser.orElseThrow(NullPointerException::new)
                            .getId(),
                    chatId,
                    null
            );
        }
        userRoleMutedPinnedChat.setUserRole(dto.getSuperuser() ? Role.ADMIN : Role.USER);
        userRoleMutedPinnedChatRepository.save(userRoleMutedPinnedChat);
        sendMessageToCorporateChat(chatId, "@" + optionalUser.get().getUsername() + " joined the group");
    }

    private void sendMessageToCorporateChat(UUID chatId, String message) {
        messageService.sendMessage(
                SendMessageDto.builder()
                        .chatId(chatId)
                        .dataDtos(List.of(
                                new MessageDataDto(
                                        MessageType.TEXT,
                                        message
                                )
                        ))
                        .build()
        );
    }

    @Override
    public void fireEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(NullPointerException::new);
        employee.setDepartment(null);
        employee.setFired(true);
        updateUserBlockedStatus(employeeId, true);
        employeeRepository.save(employee);
    }

    private void updateUserBlockedStatus(UUID employeeId, boolean blocked) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(NullPointerException::new);
        user.setBlocked(blocked);
        userRepository.save(user);
    }
}
