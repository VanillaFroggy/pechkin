package ru.intech.pechkin.messenger.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.GetPageOfUsersByDepartmentDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.GetPageOfUsersByFieldLikeDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UpdateUserIconDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UpdateUsernameDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.GetPageOfUsersByDepartmentRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.GetPageOfUsersByFieldLikeRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.UpdateUserIconRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.UpdateUsernameRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface UserRestMapper {
    UpdateUserIconDto updateUserIconRequestToDto(UpdateUserIconRequest request);

    GetPageOfUsersByFieldLikeDto getPageOfUsersByUsernameLikeRequestToDto(GetPageOfUsersByFieldLikeRequest request);

    UpdateUsernameDto updateUsernameRequestToDto(UpdateUsernameRequest request);

    GetPageOfUsersByDepartmentDto getPageOfUsersByDepartmentRequestToDto(GetPageOfUsersByDepartmentRequest request);

    GetPageOfUsersByFieldLikeDto getPageOfUsersByFieldLikeRequestToDto(GetPageOfUsersByFieldLikeRequest request);
}
