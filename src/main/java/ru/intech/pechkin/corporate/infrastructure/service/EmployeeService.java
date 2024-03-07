package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.corporate.infrastructure.service.dto.AddEmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeRegistrationResponse;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateEmployeeDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(UUID employeeId);

    List<EmployeeDto> getEmployeesByDepartment(String departmentTitle);

    List<EmployeeDto> getEmployeesByDepartmentLike(String departmentTitle);

    List<EmployeeDto> getEmployeesByFioLike(String fio);

    List<EmployeeDto> getEmployeesByPositionLike(String position);

    EmployeeRegistrationResponse addEmployee(@Valid AddEmployeeDto dto);

    void updateEmployee(@Valid UpdateEmployeeDto dto);

    void fireEmployee(UUID employeeId);
}
