package ru.intech.pechkin.corporate.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.corporate.infrastructure.service.dto.AddEmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.CreateDepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateDepartmentDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateEmployeeDto;
import ru.intech.pechkin.corporate.ui.web.rest.dto.AddEmployeeRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.CreateDepartmentRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.UpdateDepartmentRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.UpdateEmployeeRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CorporateRestMapper {
    AddEmployeeDto addEmployeeRequestToDto(AddEmployeeRequest request);

    UpdateEmployeeDto updateEmployeeRequestToDto(UpdateEmployeeRequest request);

    CreateDepartmentDto createDepartmentRequestToDto(CreateDepartmentRequest request);

    UpdateDepartmentDto updateDepartmentRequestToDto(UpdateDepartmentRequest request);
}
