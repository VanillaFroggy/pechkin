package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.AddEmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeRegistrationLinkResponse;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateEmployeeDto;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    List<EmployeeDto> getEmployeeList();

    List<EmployeeDto> getEmployeeListByDepartment(String departmentTitle);

    EmployeeDto getEmployeeById(UUID employeeId);

    EmployeeRegistrationLinkResponse addEmployee(@Valid AddEmployeeDto dto);

    void updateEmployee(@Valid UpdateEmployeeDto dto);

    void fireEmployee(UUID employeeId);
}
