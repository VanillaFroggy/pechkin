package ru.intech.pechkin.messenger.ui.web.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateGroupChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.CreateP2PChatDto;
import ru.intech.pechkin.messenger.infrastructure.service.dto.UpdateGroupChatDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.CreateGroupChatRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.CreateP2PChatRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.UpdateGroupChatRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = ComponentModel.SPRING)
public interface ChatRestMapper {
  CreateP2PChatDto createP2PChatRequestToDto(CreateP2PChatRequest request);

  CreateGroupChatDto createGroupChatRequestToDto(CreateGroupChatRequest request);

  UpdateGroupChatDto updateGroupChatRequestToDto(UpdateGroupChatRequest request);
}
