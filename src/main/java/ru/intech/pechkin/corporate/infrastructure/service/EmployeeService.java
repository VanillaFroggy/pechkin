package ru.intech.pechkin.corporate.infrastructure.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;

import java.util.UUID;

@Validated
public interface EmployeeService {
    Page<EmployeeDto> getPageOfEmployees(@Valid GetPageOfEmployeesDto dto);

    EmployeeDto getEmployeeById(@NotNull UUID employeeId);

    Page<EmployeeDto> getPageOfEmployeesByDepartment(@Valid GetPageOfEmployeesByDepartmentDto dto);

    Page<EmployeeDto> getPageOfEmployeesByDepartmentLike(@Valid GetPageOfEmployeesByFieldLikeDto dto);

    Page<EmployeeDto> getPageOfEmployeesByFioLike(@Valid GetPageOfEmployeesByFieldLikeDto dto);

    Page<EmployeeDto> getPageOfEmployeesByPositionLike(@Valid GetPageOfEmployeesByFieldLikeDto dto);

    EmployeeRegistrationResponse addEmployee(@Valid AddEmployeeDto dto);

    void updateEmployee(@Valid UpdateEmployeeDto dto);

    void fireEmployee(@NotNull UUID employeeId);
}
