package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;

import java.util.UUID;

public interface EmployeeService {
    Page<EmployeeDto> getPageOfEmployees(GetPageOfEmployeesDto dto);

    EmployeeDto getEmployeeById(UUID employeeId);

    Page<EmployeeDto> getPageOfEmployeesByDepartment(@Valid GetPageOfEmployeesByDepartmentDto dto);

    Page<EmployeeDto> getPageOfEmployeesByDepartmentLike(@Valid GetPageOfEmployeesByFieldLikeDto dto);

    Page<EmployeeDto> getPageOfEmployeesByFioLike(@Valid GetPageOfEmployeesByFieldLikeDto dto);

    Page<EmployeeDto> getPageOfEmployeesByPositionLike(@Valid GetPageOfEmployeesByFieldLikeDto dto);

    EmployeeRegistrationResponse addEmployee(@Valid AddEmployeeDto dto);

    void updateEmployee(@Valid UpdateEmployeeDto dto);

    void fireEmployee(UUID employeeId);
}
