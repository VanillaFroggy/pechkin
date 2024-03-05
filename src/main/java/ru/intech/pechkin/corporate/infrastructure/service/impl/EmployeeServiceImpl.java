package ru.intech.pechkin.corporate.infrastructure.service.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.pechkin.auth.service.exception.IllegalRegisterParameterException;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.DepartmentRepository;
import ru.intech.pechkin.corporate.infrastructure.persistence.repo.EmployeeRepository;
import ru.intech.pechkin.corporate.infrastructure.service.EmployeeService;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;
import ru.intech.pechkin.corporate.infrastructure.service.mapper.CorporateServiceMapper;
import ru.intech.pechkin.messenger.infrastructure.persistence.repo.UserRepository;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional()
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CorporateServiceMapper mapper;

    @Value("${registrationLink}")
    private String registrationLink;

    @Override
    public List<EmployeeDto> getEmployeeList() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new NoSuchElementException("There is no employees yet");
        }
        List<Department> departments = departmentRepository.findAllById(
                employees.stream()
                        .map(Employee::getDepartment)
                        .toList()
        );
        return employees
                .stream()
                .map(employee -> mapper.employeeToDto(
                        employee,
                        mapper.departmentToDto(departments.stream()
                                .filter(department -> department.getId()
                                        .equals(employee.getDepartment()))
                                .findFirst()
                                .orElseThrow(NullPointerException::new))
                ))
                .toList();
    }

    @Override
    public List<EmployeeDto> getEmployeeListByDepartment(String departmentTitle) {
        return employeeRepository.findAllByDepartment(
                        departmentRepository.findByTitle(departmentTitle)
                                .orElseThrow(NullPointerException::new)
                                .getId())
                .stream()
                .map(employee -> mapper.employeeToDto(
                        employee,
                        mapper.departmentIdAndTitleToDepartmentDto(
                                employee.getDepartment(),
                                departmentTitle
                        )
                ))
                .toList();
    }

    @Override
    public EmployeeDto getEmployeeById(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(NullPointerException::new);
        return mapper.employeeToDto(
                employee,
                mapper.departmentToDto(
                        departmentRepository.findById(employee.getDepartment())
                                .orElseThrow(NullPointerException::new)
                )
        );
    }

    @Override
    public EmployeeRegistrationLinkResponse addEmployee(@Valid AddEmployeeDto dto) {
        checkEmployeeContacts(null, dto.getEmail(), dto.getPhoneNumber());
        Employee employee = mapper.addEmployeeDtoToEntity(UUID.randomUUID(), dto, false);
        employeeRepository.save(employee);
        return new EmployeeRegistrationLinkResponse(registrationLink + "?key=" + employee.getId());
    }

    @Override
    public void updateEmployee(@Valid UpdateEmployeeDto dto) {
        checkEmployeeContacts(dto.getId(), dto.getEmail(), dto.getPhoneNumber());
        if (employeeRepository.findById(dto.getId()).isEmpty()) {
            throw new NullPointerException("Указанный работинк не найден");
        }
        if (dto.getFired()) {
            fireEmployee(dto.getId());
        }
        employeeRepository.save(mapper.updateEmployeeDtoToEntity(dto));
    }

    private void checkEmployeeContacts(UUID employeeId, String email, String phoneNumber) {
        Optional<Employee> employeeByEmail = employeeRepository.findByEmail(email);
        Optional<Employee> employeeByPhoneNumber = employeeRepository.findByPhoneNumber(phoneNumber);

        if (employeeByEmail.isPresent() && employeeByEmail.get().getId() != employeeId) {
            throw new IllegalRegisterParameterException("Этот email уже зарегестрирован");
        } else if (employeeByPhoneNumber.isPresent() && employeeByPhoneNumber.get().getId() != employeeId) {
            throw new IllegalRegisterParameterException("Этот номер телефона уже зарегестрирован");
        }
    }

    @Override
    public void fireEmployee(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(NullPointerException::new);
        employee.setFired(true);
        userService.blockUser(
                userRepository.findByEmployeeId(employeeId)
                        .orElseThrow(NullPointerException::new)
                        .getId()
        );
        employeeRepository.save(employee);
    }
}
