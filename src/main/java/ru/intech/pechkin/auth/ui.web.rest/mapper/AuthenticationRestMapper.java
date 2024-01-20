package ru.intech.pechkin.auth.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.auth.service.dto.RegisterDto;
import ru.intech.pechkin.auth.ui.web.rest.dto.AuthenticateRequest;
import ru.intech.pechkin.auth.ui.web.rest.dto.AuthenticationResponse;
import ru.intech.pechkin.auth.ui.web.rest.dto.RegisterRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AuthenticationRestMapper {
  RegisterDto registerRequestToDto(RegisterRequest request);

  AuthenticateDto authenticateRequestToDto(AuthenticateRequest request);

  AuthenticationResponse authenticationDtoToResponse(AuthenticationDto dto);
}
