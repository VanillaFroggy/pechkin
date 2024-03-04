package ru.intech.pechkin.corporate.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.corporate.infrastructure.service.dto.AddEmployeeDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.UpdateEmployeeDto;
import ru.intech.pechkin.corporate.ui.web.rest.dto.AddEmployeeRequest;
import ru.intech.pechkin.corporate.ui.web.rest.dto.UpdateEmployeeRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeRestMapper {
    AddEmployeeDto addEmployeeRequestToDto(AddEmployeeRequest request);

    UpdateEmployeeDto updateEmployeeRequestToDto(UpdateEmployeeRequest request);
}
