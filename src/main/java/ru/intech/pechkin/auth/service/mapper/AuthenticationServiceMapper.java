package ru.intech.pechkin.auth.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.auth.service.dto.AuthenticateDto;
import ru.intech.pechkin.auth.service.dto.RegisterDto;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AuthenticationServiceMapper {
  User registerDtoToEntity(RegisterDto dto);

  @Mapping(target = "username", source = "username")
  @Mapping(target = "password", source = "password")
  User authenticateDtoToEntity(AuthenticateDto dto);
}
