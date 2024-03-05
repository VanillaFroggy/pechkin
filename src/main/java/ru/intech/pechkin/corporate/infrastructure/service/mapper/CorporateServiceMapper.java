package ru.intech.pechkin.corporate.infrastructure.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CorporateServiceMapper {
    @Mapping(target = "id", source = "employee.id")
    @Mapping(target = "department", source = "departmentDto")
    EmployeeDto employeeToDto(Employee employee, DepartmentDto departmentDto);

    DepartmentDto departmentToDto(Department department);

    DepartmentDto departmentIdAndTitleToDepartmentDto(UUID departmentId, String departmentTitle);

    Employee addEmployeeDtoToEntity(UUID id, AddEmployeeDto dto, Boolean fired);

    Employee updateEmployeeDtoToEntity(UpdateEmployeeDto dto);
}
