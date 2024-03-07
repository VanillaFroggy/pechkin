package ru.intech.pechkin.corporate.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import ru.intech.pechkin.messenger.infrastructure.service.dto.MessageDataDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.SendMessageDto;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

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
    public List<EmployeeDto> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        checkListEmptiness(employees);
        return getEmployeeDtos(employees);
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
    public List<EmployeeDto> getEmployeesByDepartment(String departmentTitle) {
        DepartmentDto departmentDto = departmentService.getDepartmentByTitle(departmentTitle);
        List<Employee> employees = employeeRepository.findAllByDepartment(departmentDto.getId());
        checkListEmptiness(employees);
        return employees.stream()
                .map(employee -> mapper.employeeToDto(
                        employee,
                        departmentDto
                ))
                .toList();
    }

    @Override
    public List<EmployeeDto> getEmployeesByDepartmentLike(String departmentTitle) {
        List<DepartmentDto> departmentDtos = departmentService.getDepartmentsByTitleLike(departmentTitle);
        List<Employee> employees = employeeRepository.findAllByDepartmentIn(
                departmentDtos.stream()
                        .map(DepartmentDto::getId)
                        .toList()
        );
        checkListEmptiness(employees);
        return employees.stream()
                .map(employee -> mapper.employeeToDto(
                        employee,
                        departmentDtos.stream()
                                .filter(departmentDto ->
                                        departmentDto.getId().equals(employee.getDepartment()))
                                .findFirst()
                                .orElse(null)
                ))
                .toList();
    }

    @Override
    public List<EmployeeDto> getEmployeesByFioLike(String fio) {
        List<Employee> employees = employeeRepository.findByFioLikeIgnoreCase(fio);
        checkListEmptiness(employees);
        return getEmployeeDtos(employees);
    }

    @Override
    public List<EmployeeDto> getEmployeesByPositionLike(String position) {
        List<Employee> employees = employeeRepository.findByPositionLikeIgnoreCase(position);
        checkListEmptiness(employees);
        return getEmployeeDtos(employees);
    }

    private void checkListEmptiness(List<?> list) {
        if (list.isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
    }

    @NonNull
    private List<EmployeeDto> getEmployeeDtos(List<Employee> employees) {
        List<Department> departments = departmentRepository.findAllById(
                employees.stream()
                        .map(Employee::getDepartment)
                        .toList()
        );
        return employees
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
                .toList();
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
            throw new IllegalRegisterParameterException("Этот email уже зарегестрирован");
        } else if (employeeByPhoneNumber.isPresent() && !employeeByPhoneNumber.get().getId().equals(employeeId)) {
            throw new IllegalRegisterParameterException("Этот номер телефона уже зарегестрирован");
        }
    }

    private void removeEmployeeFromCorporateChat(UpdateEmployeeDto dto, Optional<User> optionalUser) {
        UUID chatId = chatRepository.findByDepartmentId(dto.getDepartment())
                .orElseThrow(NullPointerException::new)
                .getId();
        userRoleMutedPinnedChatRepository.deleteByUserIdAndChatId(
                optionalUser
                        .orElseThrow(NullPointerException::new)
                        .getId(),
                chatId
        );
        sendMessageToCorporateChat(chatId, "@" + optionalUser.get().getUsername() + " left the group");
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
