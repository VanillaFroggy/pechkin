package ru.intech.pechkin.auth.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.corporate.infrastructure.service.dto.EmployeeDto;
import ru.intech.pechkin.messenger.infrastructure.persistence.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AuthenticationServiceMapper {
    @Mapping(target = "token", source = "token")
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "employeeId", source = "employeeDto.id")
    @Mapping(target = "department", source = "employeeDto.department")
    AuthenticationDto userAndEmployeeDtoToAuthenticationDto(String token, User user, EmployeeDto employeeDto);
}
