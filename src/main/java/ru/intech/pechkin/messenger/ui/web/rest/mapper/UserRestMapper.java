package ru.intech.pechkin.messenger.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UpdateUserIconDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.UpdateUserIconRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface UserRestMapper {
  UpdateUserIconDto updateUserIconRequestToDto(UpdateUserIconRequest request);
}
