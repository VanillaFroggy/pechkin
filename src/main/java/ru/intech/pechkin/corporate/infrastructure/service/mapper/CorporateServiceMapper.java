package ru.intech.pechkin.corporate.infrastructure.service.mapper;

import org.mapstruct.*;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Department;
import ru.intech.pechkin.corporate.infrastructure.persistence.entity.Employee;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;

import java.util.UUID;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CorporateServiceMapper {
    @Mapping(target = "id", source = "employee.id")
    @Mapping(target = "department", source = "departmentDto")
    EmployeeDto employeeToDto(Employee employee, DepartmentDto departmentDto);

    Employee addEmployeeDtoToEntity(UUID id, AddEmployeeDto dto, Boolean fired);

    Employee updateEmployeeDtoToEntity(UpdateEmployeeDto dto);

    @Named("getDepartmentId")
    default UUID getDepartmentId(DepartmentDto departmentDto) {
        return departmentDto.getId();
    }

    @Mapping(target = "department", source = "employeeDto.department", qualifiedByName = "getDepartmentId")
    UpdateEmployeeDto employeeDtoToUpdateEmployeeDto(EmployeeDto employeeDto);

    DepartmentDto departmentToDto(Department department);

    Department createDepartmentDtoToEntity(UUID id, CreateDepartmentDto dto);

    Department updateDepartmentDtoToEntity(UpdateDepartmentDto dto);

}
