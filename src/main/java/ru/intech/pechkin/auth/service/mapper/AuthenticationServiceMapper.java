package ru.intech.pechkin.auth.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.auth.service.dto.AuthenticationDto;
import ru.intech.pechkin.messenger.infrastructure.persistance.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface AuthenticationServiceMapper {
    @Mapping(target = "token", source = "token")
    AuthenticationDto entityToDto(String token, User user);
}
