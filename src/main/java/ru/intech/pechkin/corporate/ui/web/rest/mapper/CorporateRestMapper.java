package ru.intech.pechkin.corporate.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.corporate.infrastructure.service.dto.*;
import ru.intech.pechkin.corporate.ui.web.rest.dto.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CorporateRestMapper {
    GetPageOfEmployeesDto getPageOfEmployeesRequestToDto(GetPageOfEmployeesRequest request);

    GetPageOfEmployeesByDepartmentDto getPageOfEmployeesByDepartmentRequestToDto(
            GetPageOfEmployeesByDepartmentRequest request
    );

    GetPageOfEmployeesByFieldLikeDto getPageOfEmployeesByFieldLikeRequestToDto(
            GetPageOfEmployeesByFieldLikeRequest request
    );

    AddEmployeeDto addEmployeeRequestToDto(AddEmployeeRequest request);

    UpdateEmployeeDto updateEmployeeRequestToDto(UpdateEmployeeRequest request);

    GetPageOfDepartmentsDto getPageOfDepartmentsRequestToDto(GetPageOfDepartmentsRequest request);

    CreateDepartmentDto createDepartmentRequestToDto(CreateDepartmentRequest request);

    UpdateDepartmentDto updateDepartmentRequestToDto(UpdateDepartmentRequest request);
}
