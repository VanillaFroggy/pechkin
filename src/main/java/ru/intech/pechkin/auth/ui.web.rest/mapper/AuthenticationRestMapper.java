package ru.intech.pechkin.auth.ui.web.rest.mapper;

import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.ui.web.rest.dto.AuthenticateRequest;
import ru.intech.pechkin.auth.ui.web.rest.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AuthenticationRestMapper {
  ru.intech.pechkin.auth.service.dto.RegisterDto registerRequestToDto(RegisterRequest request);

  AuthenticateDto authenticateRequestToDto(AuthenticateRequest request);
}
